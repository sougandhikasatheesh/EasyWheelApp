package com.example.easywheel

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationText: TextView

    // ✅ NEW — receive category
    private var placeType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        locationText = findViewById(R.id.locationText)

        // ✅ NEW
        placeType = intent.getStringExtra("PLACE_TYPE")

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val fused = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationText.text = "Location permission not granted"
            return
        }

        mMap.isMyLocationEnabled = true

        fused.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                val userLatLng = LatLng(loc.latitude, loc.longitude)

                // ✅ YOUR EXISTING CODE — unchanged
                mMap.addMarker(
                    MarkerOptions()
                        .position(userLatLng)
                        .title("You are here")
                )

                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(userLatLng, 16f)
                )

                locationText.text = "Location loaded on map"

                // ✅ NEW — search nearby after location ready
                placeType?.let {
                    searchNearby(getKeyword(it), loc.latitude, loc.longitude)
                }

            } else {
                locationText.text = "Location not available"
            }
        }
    }

    // ✅ NEW — map dashboard type → keyword
    private fun getKeyword(type: String): String {
        return when (type) {
            "hospital" -> "wheelchair accessible hospital"
            "pharmacy" -> "medical shop"
            "wheelchair accessible toilet" -> "accessible toilet"
            "wheelchair repair" -> "wheelchair repair"
            "disability ngo" -> "disability ngo"
            "police station" -> "police station"
            else -> type
        }
    }

    // ✅ NEW — call Places Nearby Search
    private fun searchNearby(keyword: String, lat: Double, lng: Double) {

        val url =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                    "?location=$lat,$lng" +
                    "&radius=3000" +
                    "&keyword=$keyword" +
                    "&key=${getString(R.string.google_maps_key)}"

        thread {
            try {
                val result = URL(url).readText()
                runOnUiThread {
                    addMarkers(result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ✅ NEW — add markers from JSON
    private fun addMarkers(json: String) {
        val obj = JSONObject(json)
        val results = obj.getJSONArray("results")

        for (i in 0 until results.length()) {
            val place = results.getJSONObject(i)
            val loc = place.getJSONObject("geometry")
                .getJSONObject("location")

            val lat = loc.getDouble("lat")
            val lng = loc.getDouble("lng")
            val name = place.getString("name")

            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(lat, lng))
                    .title(name)
            )
        }
    }
}
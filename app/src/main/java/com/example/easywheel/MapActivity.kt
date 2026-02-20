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

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        locationText = findViewById(R.id.locationText)

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

                mMap.addMarker(
                    MarkerOptions()
                        .position(userLatLng)
                        .title("You are here")
                )

                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(userLatLng, 16f)
                )

                locationText.text = "Location loaded on map"
            } else {
                locationText.text = "Location not available"
            }
        }
    }
}
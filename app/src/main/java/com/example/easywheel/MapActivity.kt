package com.example.easywheel

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.json.JSONObject

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationText: TextView

    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private var placeType: String? = null
    private var userLatLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        locationText = findViewById(R.id.locationText)

        // If null → opened from bottom nav (normal map)
        placeType = intent.getStringExtra("PLACE_TYPE")

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000
        ).build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationText.text = getString(R.string.location_permission_denied)
            return
        }

        mMap.isMyLocationEnabled = true

        fusedClient.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                handleLocation(loc.latitude, loc.longitude)
            } else {
                fusedClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            val l = result.lastLocation ?: return
                            handleLocation(l.latitude, l.longitude)
                            fusedClient.removeLocationUpdates(this)
                        }
                    },
                    mainLooper
                )
            }
        }

        // Marker click → open navigation in Google Maps
        mMap.setOnMarkerClickListener { marker ->

            if (marker.position != userLatLng) {

                val uri =
                    "google.navigation:q=${marker.position.latitude},${marker.position.longitude}"
                        .toUri()

                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            }

            true
        }
    }

    private fun handleLocation(lat: Double, lng: Double) {

        userLatLng = LatLng(lat, lng)

        // ------------------------------
        // Distance & ETA Algorithm Demo
        // ------------------------------
        val destinationLat = lat + 0.01
        val destinationLng = lng + 0.01

        val distance = NavigationUtils.calculateDistance(
            lat,
            lng,
            destinationLat,
            destinationLng
        )

        val eta = NavigationUtils.calculateETA(distance)

        Log.d("NavigationAlgo", "Distance: $distance km")
        Log.d("NavigationAlgo", "ETA: $eta hours")

        // ------------------------------
        // Accessibility Scoring Demo
        // ------------------------------
        val routeScore = AccessibilityRouteScorer.calculateScore(
            hasRamp = true,
            smoothPath = true,
            obstacle = false,
            stairs = false
        )

        Log.d("AccessibilityAlgo", "Route Score: $routeScore")

        // ------------------------------
        // Route Filtering Demo
        // ------------------------------
        val routes = listOf(
            "Main Road - Ramp Available",
            "Side Street - Stairs",
            "Market Road - Smooth Path"
        )

        val filteredRoutes = RouteFilter.filterAccessibleRoutes(routes)

        Log.d("FilterAlgo", "Filtered Routes: $filteredRoutes")

        // ------------------------------
        // Map UI
        // ------------------------------

        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(userLatLng!!, 15f)
        )

        mMap.addMarker(
            MarkerOptions()
                .position(userLatLng!!)
                .title(getString(R.string.you_are_here))
                .icon(
                    BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_AZURE
                    )
                )
        )

        if (placeType != null) {

            // Hide default POIs in filter mode
            mMap.setMapStyle(
                MapStyleOptions(
                    """[
                      {"featureType":"poi","stylers":[{"visibility":"off"}]},
                      {"featureType":"transit","stylers":[{"visibility":"off"}]}
                    ]"""
                )
            )

            searchNearby(getPlaceTypeParam(placeType!!), lat, lng)
        }
    }

    private fun getPlaceTypeParam(type: String): String {
        return when (type) {
            "hospital" -> "hospital"
            "pharmacy" -> "pharmacy"
            "police station" -> "police"
            "wheelchair accessible toilet" -> "public_toilet"
            "wheelchair repair" -> "hardware_store"
            "disability ngo" -> "point_of_interest"
            else -> "point_of_interest"
        }
    }

    private fun searchNearby(keyword: String, lat: Double, lng: Double) {

        // Using parameters to avoid warning
        Log.d("SearchNearby", "Type: $keyword at $lat,$lng")

        try {
            val input = assets.open("dummy_places.json")
            val json = input.bufferedReader().use { it.readText() }
            addMarkers(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addMarkers(json: String) {

        mMap.clear()

        // Re-add user marker
        userLatLng?.let {
            mMap.addMarker(
                MarkerOptions()
                    .position(it)
                    .title(getString(R.string.you_are_here))
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_AZURE
                        )
                    )
            )
        }

        val obj = JSONObject(json)
        val results = obj.getJSONArray("results")

        locationText.text = getString(
            R.string.places_found,
            results.length()
        )

        val hue = when (placeType) {
            "hospital" -> BitmapDescriptorFactory.HUE_RED
            "pharmacy" -> BitmapDescriptorFactory.HUE_GREEN
            "wheelchair accessible toilet" -> BitmapDescriptorFactory.HUE_ORANGE
            "wheelchair repair" -> BitmapDescriptorFactory.HUE_VIOLET
            "disability ngo" -> BitmapDescriptorFactory.HUE_YELLOW
            "police station" -> BitmapDescriptorFactory.HUE_BLUE
            else -> BitmapDescriptorFactory.HUE_ROSE
        }

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
                    .icon(BitmapDescriptorFactory.defaultMarker(hue))
            )
        }
    }
}
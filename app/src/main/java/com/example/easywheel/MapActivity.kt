package com.example.easywheel

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.InputStreamReader

// Mock data models
data class MockPlace(
    val name: String,
    val lat: Double,
    val lng: Double,
    val hasRamp: Boolean,
    val smoothPath: Boolean,
    val stairs: Boolean,
    val obstacle: Boolean
)

data class MockPlacesData(
    val hospital: List<MockPlace>,
    @SerializedName("police_station") val policeStation: List<MockPlace>,
    @SerializedName("ngo") val ngo: List<MockPlace>,
    @SerializedName("repair_shop") val repairShop: List<MockPlace>,
    val toilet: List<MockPlace>
)

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationText: TextView
    private lateinit var fusedClient: FusedLocationProviderClient
    private var placeType: String? = null
    private var userLatLng: LatLng? = null
    private lateinit var mockData: MockPlacesData

    private val apiKey: String = "AIzaSyB6VkogZkNOdwJ0K8QcLJYfbjCrvg9fBwA" // replace with your key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        locationText = findViewById(R.id.locationText)
        placeType = intent.getStringExtra("PLACE_TYPE")
        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        // Load mock JSON
        mockData = loadMockData()

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationText.text = "Location permission denied"
            return
        }

        mMap.isMyLocationEnabled = true

        fusedClient.lastLocation.addOnSuccessListener { loc ->
            loc?.let {
                userLatLng = LatLng(it.latitude, it.longitude)
                handleLocation(it.latitude, it.longitude)
            }
        }

        mMap.setOnMarkerClickListener { marker ->
            if (marker.position != userLatLng) {
                val uri = "google.navigation:q=${marker.position.latitude},${marker.position.longitude}".toUri()
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            }
            true
        }
    }

    private fun handleLocation(lat: Double, lng: Double) {
        userLatLng = LatLng(lat, lng)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng!!, 14f))

        // Add user marker
        mMap.addMarker(
            MarkerOptions()
                .position(userLatLng!!)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )

        // Show toast to confirm category selection
        if (!placeType.isNullOrEmpty()) {
            fetchNearbyPlacesMock(placeType ?: "")
        }
    }

    private fun fetchNearbyPlacesMock(category: String) {
        val places: List<MockPlace> = when (category.lowercase()) {
            "hospital" -> mockData.hospital
            "police station" -> mockData.policeStation
            "ngo","disability ngo" -> mockData.ngo
            "wheelchair repair" -> mockData.repairShop
            "wheelchair accessible toilet" -> mockData.toilet
            else -> emptyList()
        }

        // Show toast if no places found
        if (places.isEmpty()) {
            Toast.makeText(this, "No places found for $category", Toast.LENGTH_SHORT).show()
        }

        displayPlaces(places)
    }

    private fun displayPlaces(places: List<MockPlace>) {
        if (places.isEmpty()) {
            locationText.text = "No places found"
            return
        }

        mMap.clear()
        userLatLng?.let {
            mMap.addMarker(
                MarkerOptions()
                    .position(it)
                    .title("You are here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )
        }

        val scoredPlaces = places.map { place ->
            val distance = NavigationUtils.calculateDistance(
                userLatLng!!.latitude,
                userLatLng!!.longitude,
                place.lat,
                place.lng
            )
            val score = AccessibilityRouteScorer.calculateScore(
                hasRamp = place.hasRamp,
                smoothPath = place.smoothPath,
                obstacle = place.obstacle,
                stairs = place.stairs
            )
            Triple(place, distance, score.toDouble())
        }

        val bestPlace = scoredPlaces.maxByOrNull { it.third }

        // Highlight best place
        bestPlace?.let { (place, _, score) ->
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(place.lat, place.lng))
                    .title("BEST: ${place.name}\nScore: ${"%.2f".format(score)}")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
            Toast.makeText(this, "Best place: ${place.name}", Toast.LENGTH_LONG).show()
        }

        // Add remaining places
        scoredPlaces.filter { it != bestPlace }.forEach { (place, distance, score) ->
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(place.lat, place.lng))
                    .title("${place.name}\n${"%.2f".format(distance)} km, Score: ${"%.2f".format(score)}")
                    .icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor()))
            )
        }

        locationText.text = "Found ${places.size} places"
    }

    private fun getMarkerColor(): Float {
        return when (placeType?.lowercase()) {
            "hospital" -> BitmapDescriptorFactory.HUE_RED
            "pharmacy" -> BitmapDescriptorFactory.HUE_GREEN
            "police station" -> BitmapDescriptorFactory.HUE_BLUE
            "wheelchair accessible toilet" -> BitmapDescriptorFactory.HUE_ORANGE
            "wheelchair repair" -> BitmapDescriptorFactory.HUE_VIOLET
            "disability ngo" -> BitmapDescriptorFactory.HUE_YELLOW
            else -> BitmapDescriptorFactory.HUE_ROSE
        }
    }

    private fun loadMockData(): MockPlacesData {
        val inputStream = assets.open("dummydata.json") // ensure lowercase filename
        val reader = InputStreamReader(inputStream)
        return Gson().fromJson(reader, MockPlacesData::class.java)
    }
}
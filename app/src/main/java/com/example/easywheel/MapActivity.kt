package com.example.easywheel

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult


class MapActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationText: TextView

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        locationText = findViewById(R.id.locationText)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        // If permission is already granted, fetch location
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            // Launch permission request dialog
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates = 3 // request multiple updates for better accuracy
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val locations = result.locations
                if (locations.isNotEmpty()) {
                    // Take the most recent location
                    val location = locations.last()
                    val lat = location.latitude
                    val lon = location.longitude

                    // Convert to address
                    val geocoder = Geocoder(this@MapActivity, Locale.getDefault())
                    try {
                        val addresses = geocoder.getFromLocation(lat, lon, 1)
                        val address = if (!addresses.isNullOrEmpty()) addresses[0].getAddressLine(0) else "Address not found"

                        locationText.text = address

                        val resultIntent = Intent()
                        resultIntent.putExtra("address", address)
                        setResult(RESULT_OK, resultIntent)
                        finish() // close activity

                    } catch (e: IOException) {
                        e.printStackTrace()
                        locationText.text = "Error fetching address"
                    }

                } else {
                    Toast.makeText(this@MapActivity, "Unable to get location. Try again.", Toast.LENGTH_LONG).show()
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }


}







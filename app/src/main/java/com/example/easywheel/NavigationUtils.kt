package com.example.easywheel

import kotlin.math.*

object NavigationUtils {

    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {

        val R = 6371 // Earth radius in KM
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }

    fun calculateETA(distanceKm: Double): Double {
        val avgSpeed = 5.0 // walking speed km/h
        return distanceKm / avgSpeed
    }
}
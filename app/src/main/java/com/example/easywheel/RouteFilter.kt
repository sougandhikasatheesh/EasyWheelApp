package com.example.easywheel

object RouteFilter {

    fun filterAccessibleRoutes(routes: List<String>): List<String> {
        return routes.filter {
            !it.contains("Stairs", ignoreCase = true)
        }
    }
}
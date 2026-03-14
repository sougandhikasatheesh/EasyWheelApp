package com.example.easywheel.utils;

import java.util.ArrayList;
import java.util.List;

public class RouteManager {

    public static String getShortestRoute(List<String> routes) {

        if (routes == null || routes.size() == 0) {
            return null;
        }

        String shortestRoute = routes.get(0);

        for (String route : routes) {

            if (route.length() < shortestRoute.length()) {
                shortestRoute = route;
            }
        }

        return shortestRoute;
    }
}
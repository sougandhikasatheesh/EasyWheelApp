package com.example.easywheel.algorithm;

import java.util.ArrayList;
import java.util.List;

public class RouteFilter {

    public static List<String> filterAccessibleRoutes(List<String> routes) {

        List<String> filteredRoutes = new ArrayList<>();

        for (String route : routes) {

            // Example filtering logic
            if (!route.toLowerCase().contains("stairs")) {
                filteredRoutes.add(route);
            }
        }

        return filteredRoutes;
    }
}
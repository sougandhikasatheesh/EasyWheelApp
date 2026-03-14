package com.example.easywheel.algorithm;

public class AccessibilityRouteScorer {

    // Score route based on accessibility features
    public static int calculateScore(boolean hasRamp,
                                     boolean smoothPath,
                                     boolean obstacle,
                                     boolean stairs) {

        int score = 50; // base score

        if (hasRamp) {
            score -= 10;
        }

        if (smoothPath) {
            score -= 5;
        }

        if (obstacle) {
            score += 20;
        }

        if (stairs) {
            score += 100;
        }

        return score;
    }
}
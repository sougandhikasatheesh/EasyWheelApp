package com.example.easywheel

object AccessibilityRouteScorer {

    fun calculateScore(
        hasRamp: Boolean,
        smoothPath: Boolean,
        obstacle: Boolean,
        stairs: Boolean
    ): Int {

        var score = 100

        if (!hasRamp) score -= 30
        if (!smoothPath) score -= 20
        if (obstacle) score -= 25
        if (stairs) score -= 40

        return score
    }
}
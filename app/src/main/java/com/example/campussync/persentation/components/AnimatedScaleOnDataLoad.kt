package com.example.campussync.persentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

@Composable
fun AnimatedScaleOnDataLoad(
    isDataLoaded: Boolean
): Float {
    val targetScale = if (isDataLoaded) 1f else 0.90f
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 1000),
        visibilityThreshold = 0.01f,
        label = "Scale on Data Loaded"
    )
    return scale
}

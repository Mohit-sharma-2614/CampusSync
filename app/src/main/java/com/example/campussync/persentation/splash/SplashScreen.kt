package com.example.campussync.persentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.campussync.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen(
    navigateToLoginScreen: () -> Unit,
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val splashState by viewModel.splashState.collectAsState()

    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (splashState.isLoading) 0.5f else 1f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "alpha"
    )
    val entranceAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800, delayMillis = 200),
        label = "entranceAlpha"
    )

    // Gradient background using Material Design 3 colors
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.tertiaryContainer
        ),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(0f, Float.POSITIVE_INFINITY)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Logo with shadow and animation
//            Icon(
//                painter = painterResource(R.drawable.campus_sync_logo), // Placeholder logo
//                contentDescription = "App Logo",
//                tint = MaterialTheme.colorScheme.onPrimary,
//                modifier = Modifier
//                    .size(120.dp)
//                    .scale(scale)
//                    .alpha(entranceAlpha)
//                    .graphicsLayer {
//                        shadowElevation = 8f
//                        shape = CircleShape
//                        clip = true
//                    }
//            )
//            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Welcome to CampusSync",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(entranceAlpha)
                    .graphicsLayer {
                        shadowElevation = 4f
                    }
            )
            Spacer(modifier = Modifier.height(32.dp))
            if (splashState.isLoading) {
                val progressScale by infiniteTransition.animateFloat(
                    initialValue = 0.9f,
                    targetValue = 1.1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "progressScale"
                )
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp,
                    modifier = Modifier
                        .size(48.dp)
                        .scale(progressScale)
                        .alpha(alpha),
                    trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                )
            }
        }
    }

    LaunchedEffect(splashState.navigateTo) {
        splashState.navigateTo?.let { destination ->
            navController.navigate(destination) {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
}

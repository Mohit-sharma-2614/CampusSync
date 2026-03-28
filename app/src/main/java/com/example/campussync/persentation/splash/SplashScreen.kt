package com.example.campussync.persentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun SplashScreen(
    navigateToLoginScreen: () -> Unit,
    navController: NavController = NavController(LocalContext.current),
    viewModel: SplashViewModel = hiltViewModel()
) {
    val splashState by viewModel.splashState.collectAsState()

    SplashComponent(
        splashState = splashState,
//        navigateToLoginScreen = navigateToLoginScreen,
        navController = navController
    )
}

@Composable
fun SplashComponent(
    splashState: SplashState,
//    navigateToLoginScreen: () -> Unit,
    navController: NavController
){
    // --- Animations ---
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    // 1. Pulsing Logo Scale
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // 2. Rotation for Loading (Syncing effect)
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    // 3. Entrance Fade
    val entranceAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000),
        label = "entrance"
    )

    // --- Design Elements ---

    // A subtle, professional gradient
    val backgroundBrush = Brush.radialGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
        center = Offset.Unspecified,
        radius = Float.POSITIVE_INFINITY
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .alpha(entranceAlpha)
        ) {

            // --- Logo Section ---
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                // Outer Ring (Only visible when loading to simulate "Syncing")
                if (splashState.isLoading) {
                    CircularProgressIndicator(
                        progress = { 1f }, // 100% static ring, we rotate the modifier
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(rotation), // Rotate the ring
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        strokeWidth = 2.dp,
                        trackColor = Color.Transparent,
                        strokeCap = StrokeCap.Round,
                    )

                    // Rotating Dash
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(rotation),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp,
                        trackColor = Color.Transparent,
                        strokeCap = StrokeCap.Round
                    )
                }

                // Inner Logo Container
                Surface(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(pulseScale),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shadowElevation = 10.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // Placeholder Icon - Replace with:
                        // painterResource(R.drawable.campus_sync_logo)
                        Icon(
                            imageVector = Icons.Rounded.School,
                            contentDescription = "Logo",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))


            Text(
                text = "CampusSync",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Connect. Collaborate. Succeed.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.5.sp
            )
        }

        // --- Footer ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(0.6f)
        ) {
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    // --- Navigation Logic (Unchanged) ---
    LaunchedEffect(splashState.navigateTo) {
        splashState.navigateTo?.let { destination ->
            navController.navigate(destination) {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSplashScreen(){
    SplashComponent(
        splashState = SplashState(),
//        navigateToLoginScreen = {},
        navController = NavController(LocalContext.current)
    )
}
package com.example.campussync.navigation

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.campussync.CampusSyncViewModel
import com.example.campussync.R
import com.example.campussync.data.entity.state.SessionState
import com.example.campussync.data.observer.NetworkObserver
import com.example.campussync.persentation.auth.AuthScreen
import com.example.campussync.persentation.dashboard.DashboardScreen

@Composable
fun AnimatedLoadingBar() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_animation")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_pulse"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_pulse"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Identity with pulsing animation
        Text(
            text = "CampusSync",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .alpha(alpha)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Modern circular indicator
        CircularProgressIndicator(
            modifier = Modifier.size(56.dp),
            color = MaterialTheme.colorScheme.secondary,
            strokeWidth = 4.dp,
            strokeCap = StrokeCap.Round,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Synchronizing your experience...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.alpha(0.7f)
        )
    }
}

@Composable
fun ShowOffline(
    onRetryClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.offline_24),
                        contentDescription = "Offline",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text("You are offline", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Please check your internet connection and try again",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.alpha(0.8f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onRetryClick() },
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
fun RootNavigation(
    viewModel: CampusSyncViewModel,
    navController: NavHostController
) {
    val appState by viewModel.appState.collectAsState()
    val sessionState = appState.session
    val networkStatus = appState.network

    LaunchedEffect(sessionState) {
        Log.d("RootNavigation", "Session state changed: $sessionState")
        when (sessionState) {
            SessionState.Authenticated -> {
                navController.navigate("dashboard") {
                    popUpTo(0) { inclusive = true }
                }
            }

            SessionState.Unauthenticated -> {
                navController.navigate("auth") {
                    popUpTo(0) { inclusive = true }
                }
            }

            SessionState.Loading -> {
                if (navController.currentDestination?.route != "loading") {
                    navController.navigate("loading") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    if (networkStatus == NetworkObserver.Status.Lost) {
        Log.d("RootNavigation", "Network status changed: $networkStatus")
        ShowOffline(
            onRetryClick = { viewModel.onRetryClick() }
        )
    } else {
        Log.d("RootNavigation", "Network status changed: $networkStatus")
        NavHost(
            navController = navController,
            startDestination = "loading"
        ) {
            composable("loading") {
                AnimatedLoadingBar()
            }

            composable("auth") {
                AuthScreen(navController)
            }

            composable("dashboard") {
                DashboardScreen()
//                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    Text("Welcome to CampusSync")
//                }
            }
        }
    }
}

package com.example.campussync.navigation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.campussync.CampusSyncViewModel
import com.example.campussync.R
import com.example.campussync.data.entity.state.SessionState
import com.example.campussync.data.observer.NetworkObserver
import com.example.campussync.persentation.auth.AuthScreen


@Composable
fun ShowOffline(){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp,
                focusedElevation = 8.dp,
                hoveredElevation = 8.dp,
                disabledElevation = 0.dp,
                draggedElevation = 8.dp,
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("You are offline", style = MaterialTheme.typography.titleLarge)
                Text("Please check your internet connection and try again")
                Image(
                    painter = painterResource(id = R.drawable.offline_24),
                    contentDescription = "Offline",
                    modifier = Modifier.padding(16.dp),
                    alignment = Alignment.Center,
                    alpha = 0.5f
                )
            }
        }
    }
}

@Composable
fun RootNavigation(
    viewModel: CampusSyncViewModel,
    navController: NavHostController
) {
    val networkState = viewModel.appState.collectAsState().value.network
    val sessionState = viewModel.appState.collectAsState().value.session

    LaunchedEffect(sessionState) {
        Log.d("RootNavigation", "RootNavigation:Session state changed: $sessionState")
        when (sessionState) {
            SessionState.Authenticated -> {
                navController.navigate("dashboard") {
                    popUpTo("auth") { inclusive = true }
                }
            }

            SessionState.Unauthenticated -> {
                navController.navigate("auth") {
                    popUpTo("dashboard") { inclusive = true }
                }
            }

            SessionState.Loading -> Unit
        }
    }

    if(networkState != NetworkObserver.Status.Available) {
        ShowOffline()
    } else {
        NavHost(
            navController = navController,
            startDestination = "loading"
        ) {
            composable("loading") {
                // Loading
            }

            composable("auth") {
                AuthScreen(navController)
            }

            composable("dashboard") {
                // DashboardScreen(navController)
            }
        }
    }
}
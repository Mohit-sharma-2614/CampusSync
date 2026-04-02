package com.example.campussync.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.campussync.CampusSyncViewModel
import com.example.campussync.data.entity.state.SessionState
import com.example.campussync.persentation.auth.AuthScreen

@Composable
fun RootNavigation(
    viewModel: CampusSyncViewModel,
    navController: NavHostController
) {
    val sessionState = viewModel.appState.collectAsState().value.session

    LaunchedEffect(sessionState) {
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
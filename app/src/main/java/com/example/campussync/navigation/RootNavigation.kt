package com.example.campussync.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.campussync.CampusSyncViewModel
import com.example.campussync.data.entity.state.SessionState
import com.example.campussync.data.observer.NetworkObserver
import com.example.campussync.persentation.auth.AuthScreen
import com.example.campussync.persentation.dashboard.DashboardScreen
import com.example.campussync.persentation.profile.ProfileScreen
import com.example.campussync.persentation.qr.QrScreen
import com.example.campussync.utils.AppBottomBar
import com.example.campussync.utils.helper.shouldShowBottomBar
import com.example.campussync.utils.views.AnimatedLoadingBar
import com.example.campussync.utils.views.AppScaffold
import com.example.campussync.utils.views.OfflineContent


@Composable
fun RootNavigation(
    viewModel: CampusSyncViewModel,
    navController: NavHostController
) {
    val appState by viewModel.appState.collectAsState()
    val topBarConfig by viewModel.topBarConfig.collectAsState()
    val sessionState = appState.session
    val networkStatus = appState.network

    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route

    AppScaffold(
        topBarConfig = topBarConfig,
        bottomBar = {
            if(shouldShowBottomBar(currentRoute)) {
                AppBottomBar(
                    items = bottomNavItems,
                    currentRoute = currentRoute,
                    onItemClick = { item ->
                        navController.navigate(item.route){
                            popUpTo(Dashboard.route)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) {padding ->
        if (networkStatus == NetworkObserver.Status.Lost) {
            Log.d("RootNavigation", "Network status changed: $networkStatus")
            OfflineContent(
                onRetryClick = { viewModel.onRetryClick() }
            )
        } else {
            Log.d("RootNavigation", "Network status changed: $networkStatus")
            NavHost(
                navController = navController,
                startDestination = if(sessionState == SessionState.Authenticated) Dashboard.route else Login.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(LoadingRoute.route) {
                    AnimatedLoadingBar()
                }

                composable(Login.route) {
                    AuthScreen(navController)
                }

                composable(Profile.route) {
                    ProfileScreen {
                        viewModel.setTopBar(it)
                    }
                }

                composable(QrCodeScanner.route) {
                    QrScreen {
                        viewModel.setTopBar(it)
                    }
                }

                composable(Dashboard.route) {
                    DashboardScreen(
                        setTopBar = { viewModel.setTopBar(it) },
                        onCardClick = {
                            navController.navigate(it.destination)
                        },
                        onLogOutClick = {
                            navController.navigate(Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }

    LaunchedEffect(sessionState) {
        Log.d("RootNavigation", "Session state changed: $sessionState")
        when (sessionState) {
            SessionState.Authenticated -> {
                navController.navigate(Dashboard.route) {
                    popUpTo(0) { inclusive = true }
                }
            }

            SessionState.Unauthenticated -> {
                navController.navigate(Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }

            SessionState.Loading -> {
                if (navController.currentDestination?.route != LoadingRoute.route) {
                    navController.navigate(LoadingRoute.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }
}

package com.example.campussync.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.campussync.ui.screens.attendence.AttendanceScreen
import com.example.campussync.ui.screens.classes.ClassesScreen
import com.example.campussync.ui.screens.dashboard.DashboardScreen
import com.example.campussync.ui.screens.profile.ProfileScreen
import com.example.campussync.ui.screens.splash.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SPLASH_SCREEN) {
        composable(Routes.SPLASH_SCREEN) {
            SplashScreen(
                navController,
                onSplashComplete = {
                    navController.navigate(Routes.DASHBOARD_SCREEN) {
                        popUpTo(Routes.SPLASH_SCREEN) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.DASHBOARD_SCREEN) {
            DashboardScreen(
                navController = navController,
                onCardClick = { card ->
                    navController.navigate(card.destination)
                },
            )
        }

        composable(Routes.CLASSES_NAV) {
            ClassesScreen(
                navController = navController,
                onBackClick = {
                    navController.navigate(Routes.HOME_NAV)
                },
            )
        }

        composable(Routes.PROFILE_NAV) {
            ProfileScreen(
                navController = navController,
                onBackClick = {
                    navController.navigate(Routes.HOME_NAV)
                },
            )
        }

        composable(Routes.ATTENDANCE_SCREEN) {
            AttendanceScreen(
                navController = navController,
                onBackClick = {
                    navController.navigate(Routes.HOME_NAV)
                }
            )
        }
    }
}
package com.example.campussync.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsStartWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.campussync.persentation.components.BottomNavigationComponent

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id){
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }


// Generic type function that is use to navigate with arguments
fun NavHostController.navigateWithArgs(
    baseRoute: String,
    vararg pathArgs: Any?
) {
    val fullRoute = buildString {
        append(baseRoute)
        pathArgs.forEach { arg ->
            append("/$arg")
        }
    }
    this.navigateSingleTopTo(fullRoute)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = bottomNavItems.find { it.route == currentDestination?.route } ?: Dashboard

    Scaffold(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.background
            ),
        bottomBar = {
            if(bottomNavItems.any { it.route == currentDestination?.route }){
                BottomNavigationComponent(
                    allScreens = bottomNavItems,
                    onTabSelected = { newScreen ->
                        navController
                            .navigate(newScreen.route){
                                popUpTo(Dashboard.route){
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                    },
                    currentScreen = currentScreen,
                )
            }
        }
    ) {
        AppNavHost(
            navController = navController,
            modifier = Modifier
        )
    }
}

@Preview
@Composable
fun PreviewFun(){
    AppNavigation()
}
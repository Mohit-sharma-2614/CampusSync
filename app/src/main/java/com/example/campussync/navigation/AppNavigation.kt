package com.example.campussync.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.campussync.data.observer.NetworkObserver
import com.example.campussync.persentation.components.BottomNavigationComponent
import kotlinx.coroutines.flow.StateFlow
import org.koin.compose.getKoin

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

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation() {

    val networkState: StateFlow<NetworkObserver.Status> = getKoin().get()
    val status by networkState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(status) {
        if (status == NetworkObserver.Status.Unavailable) {
            snackbarHostState.showSnackbar("No Internet Connection")
        } else if (status == NetworkObserver.Status.Available) {
            snackbarHostState.showSnackbar("Internet Connection found")
        } else if (status == NetworkObserver.Status.Lost) {
            snackbarHostState.showSnackbar("Internet Connection Lost")
        }
    }

    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = bottomNavItems.find { it.route == currentDestination?.route } ?: Dashboard

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview
@Composable
fun PreviewFun(){
    AppNavigation()
}
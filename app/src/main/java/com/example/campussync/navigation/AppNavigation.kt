package com.example.campussync.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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

// These are the functions using the generic function
fun NavHostController.navigateToDashboard(isTeacher: Boolean, id: Long){
    this.navigateWithArgs(Dashboard.route,isTeacher,id)
}

fun NavHostController.navigateToProfile(isTeacher: Boolean, id: Long){
    this.navigateWithArgs(Profile.route,isTeacher,id)
}

fun NavHostController.navigateToClasses(isTeacher: Boolean, id: Long){
    this.navigateWithArgs(Classes.route,isTeacher,id)
}

fun NavHostController.navigateToAttendance(isTeacher: Boolean, id: Long){
    this.navigateWithArgs(AttendanceRoute.route,isTeacher,id)
}

fun NavHostController.navigateToAssignments(isTeacher: Boolean, id: Long){
    this.navigateWithArgs(AssignmentsRoute.route,isTeacher,id)
}
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = bottomNavItems.find { it.route == currentDestination?.route } ?: Dashboard

    Scaffold(
        bottomBar = {
            BottomNavigationComponent(
                allScreens = bottomNavItems,
                onTabSelected = { newScreen ->
                    navController
                        .navigate(newScreen.route){
                            popUpTo(navController.graph.startDestinationId){
                                saveState = true
                                inclusive = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                },
                currentScreen = currentScreen
            )
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview
@Composable
fun PreviewFun(){
    AppNavigation()
}
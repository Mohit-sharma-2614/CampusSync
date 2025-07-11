package com.example.campussync.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Class
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument

interface Routes {
    val name: String
    val route: String
    val selectedIcon: ImageVector

}

object Dashboard: Routes{
    override val selectedIcon = Icons.Filled.Dashboard
    val unselectedIcon = Icons.Outlined.Dashboard
    override val name = "Dashboard"
    override val route = "dashboard"
    val idTypeArg = "id"
    val isTeacherTypeArg = "isTeacher"
    val routeWithArgs = "${route}/{$isTeacherTypeArg}/{$idTypeArg}"
    fun createRoute(isTeacher: Boolean, id: Long) = "$route/$isTeacher/$id"
    val argument = listOf(
        navArgument(idTypeArg, builder = { type = NavType.LongType }),
        navArgument(isTeacherTypeArg, builder = { type = NavType.BoolType })
    )
}

object Classes: Routes {
    override val selectedIcon = Icons.Filled.Class
    val unselectedIcon = Icons.Outlined.Class
    override val name = "Classes"
    override val route = "classes"
    val idTypeArg = "id"
    val isTeacherTypeArg = "isTeacher"
    val routeWithArgs = "${Classes.route}/{$isTeacherTypeArg}/{$idTypeArg}"
    fun createRoute(isTeacher: Boolean, id: Long) = "${Dashboard.route}/$isTeacher/$id"
    val argument = listOf(
        navArgument(idTypeArg, builder = { type = NavType.LongType }),
        navArgument(isTeacherTypeArg, builder = { type = NavType.BoolType })
    )
}

object Profile: Routes {
    override val selectedIcon = Icons.Filled.Person
    val unselectedIcon = Icons.Outlined.Person
    override val name = "Profile"
    override val route = "profile"
    val idTypeArg = "id"
    val isTeacherTypeArg = "isTeacher"
    val routeWithArgs = "${Profile.route}/{$isTeacherTypeArg}/{$idTypeArg}"
    fun createRoute(isTeacher: Boolean, id: Long) = "${Dashboard.route}/$isTeacher/$id"
    val argument = listOf(
        navArgument(idTypeArg, builder = { type = NavType.LongType }),
        navArgument(isTeacherTypeArg, builder = { type = NavType.BoolType })
    )
}

object AttendanceRoute: Routes {
    // Added for simplicity, this icon will not in fact be used, as SingleAccount isn't
    // part of the Bottom Nav Bar
    override val selectedIcon = Icons.Filled.Class
    override val name = "Attendance"
    override val route = "attendance"
    val idTypeArg = "id"
    val isTeacherTypeArg = "isTeacher"
    val routeWithArgs = "${AttendanceRoute.route}/{$isTeacherTypeArg}/{$idTypeArg}"
    fun createRoute(isTeacher: Boolean, id: Long) = "${Dashboard.route}/$isTeacher/$id"
    val argument = listOf(
        navArgument(idTypeArg, builder = { type = NavType.LongType }),
        navArgument(isTeacherTypeArg, builder = { type = NavType.BoolType })
    )
}

object AssignmentsRoute: Routes {
    // Added for simplicity, this icon will not in fact be used, as SingleAccount isn't
    // part of the Bottom Nav Bar
    override val selectedIcon = Icons.Filled.Class
    override val name = "Assignments"
    override val route = "assignments"
    val idTypeArg = "id"
    val isTeacherTypeArg = "isTeacher"
    val routeWithArgs = "${AssignmentsRoute.route}/{$isTeacherTypeArg}/{$idTypeArg}"
    fun createRoute(isTeacher: Boolean, id: Long) = "${Dashboard.route}/$isTeacher/$id"
    val argument = listOf(
        navArgument(idTypeArg, builder = { type = NavType.LongType }),
        navArgument(isTeacherTypeArg, builder = { type = NavType.BoolType })
    )
}

object Splash: Routes {
    // Added for simplicity, this icon will not in fact be used, as SingleAccount isn't
    // part of the Bottom Nav Bar
    override val selectedIcon = Icons.Filled.Class
    override val name = "Splash"
    override val route = "splash"
}

object Login : Routes {
    // Added for simplicity, this icon will not in fact be used, as SingleAccount isn't
    // part of the Bottom Nav Bar
    override val selectedIcon = Icons.Filled.Class
    override val name = "Login"
    override val route = "login"
}

val dashboardRoutes = listOf(AssignmentsRoute, AttendanceRoute)
val bottomNavItems = listOf(Dashboard, Classes, Profile)

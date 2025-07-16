package com.example.campussync.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.automirrored.outlined.Subject
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.outlined.Class
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Subject
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument

// Your existing Routes interface and objects
interface Routes {
    val name: String
    val route: String
    val selectedIcon: ImageVector
    val unselectedIcon: ImageVector
}

object Dashboard : Routes {
    override val selectedIcon = Icons.Filled.Dashboard
    override val unselectedIcon = Icons.Outlined.Dashboard
    override val name = "Dashboard"
    override val route = "dashboard"
}

object Classes : Routes {
    override val selectedIcon = Icons.Filled.Class
    override val unselectedIcon = Icons.Outlined.Class
    override val name = "Classes"
    override val route = "classes"
}

object Profile : Routes {
    override val selectedIcon = Icons.Filled.Person
    override val unselectedIcon = Icons.Outlined.Person
    override val name = "Profile"
    override val route = "profile"
}

object AttendanceRoute : Routes {
    override val selectedIcon = Icons.Filled.Class
    override val unselectedIcon = Icons.Outlined.Class
    override val name = "Attendance"
    override val route = "attendance"
}

object AssignmentsRoute : Routes {
    override val selectedIcon = Icons.Filled.Class
    override val unselectedIcon = Icons.Outlined.Class
    override val name = "Assignments"
    override val route = "assignments"
}

object Splash : Routes {
    override val selectedIcon = Icons.Filled.Class
    override val unselectedIcon = Icons.Outlined.Class
    override val name = "Splash"
    override val route = "splash"
}

object Login : Routes {
    override val selectedIcon = Icons.Filled.Class
    override val unselectedIcon = Icons.Outlined.Class
    override val name = "Login"
    override val route = "login"
}

// NEW NAVIGATION ROUTES
object SubjectAttendanceScreenRoute : Routes {
    override val selectedIcon = Icons.AutoMirrored.Filled.Subject // Or any other suitable icon
    override val unselectedIcon = Icons.AutoMirrored.Outlined.Subject // Or any other suitable icon
    override val name = "Subject Attendance"
    val subjectIdArg = "subjectId" // Argument key
    override val route = "subject_attendance"
    val routeWithArgs = "$route/{$subjectIdArg}"
    val arguments = listOf(
        navArgument(subjectIdArg) { type = NavType.LongType }
    )
    fun createRoute(subjectId: Long) = "$route/$subjectId"
}

object StudentQrScannerRoute : Routes {
    override val unselectedIcon = Icons.Outlined.QrCode // Icon for QR Scanner
    override val selectedIcon = Icons.Filled.QrCode // Icon for QR Scanner
    override val name = "QR Scanner"
    override val route = "student_qr_scanner"
    // No direct arguments needed as ViewModel fetches user data
}

object TeacherQrGeneratorRoute : Routes {
    override val selectedIcon = Icons.Filled.QrCode // Icon for QR Generator (can be same as scanner)
    override val unselectedIcon = Icons.Outlined.QrCode // Icon for QR Generator (can be same as scanner)
    override val name = "QR Generator"
    override val route = "teacher_qr_generator"
    // No direct arguments needed as ViewModel fetches subjects and user data
}


val dashboardRoutes = listOf(AssignmentsRoute, AttendanceRoute)
val bottomNavItems = listOf(Dashboard, Classes, Profile)
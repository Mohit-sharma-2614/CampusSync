package com.example.campussync.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.campussync.persentation.assignment.AssignmentScreen
import com.example.campussync.persentation.attendance.AttendanceScreen
import com.example.campussync.persentation.attendance.attendanceqr.StudentQrScannerScreen
import com.example.campussync.persentation.attendance.attendanceqr.TeacherQrGeneratorScreen
import com.example.campussync.persentation.attendance.subject.SubjectAttendanceScreen
import com.example.campussync.persentation.auth.AuthScreen
import com.example.campussync.persentation.classes.ClassesScreen
import com.example.campussync.persentation.dashboard.DashboardScreen
import com.example.campussync.persentation.profile.ProfileScreen
import com.example.campussync.persentation.splash.SplashScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Splash.route,
        modifier = modifier
    ) {

        composable(
            Splash.route
        ) {
            SplashScreen(
                navigateToLoginScreen = {
                    navController.navigate(Login.route) {
                        popUpTo(Splash.route) { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        composable(
            Login.route
        ) {
            AuthScreen(
                navController = navController
            )
        }

        composable(
            route = Dashboard.route,
        ) {
            DashboardScreen(
                onCardClick = { card ->
                    val targetRoute = when (card.destination) {
                        AttendanceRoute.route -> AttendanceRoute.route
                        AssignmentsRoute.route -> AssignmentsRoute.route
                        else -> card.destination // fallback
                    }
                    navController.navigate(targetRoute)
                },
                onLogOutClick = {
                    navController.navigate(Login.route) {
                        popUpTo(Dashboard.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToLoginScreen = {
                    navController.navigate(Login.route) {
                        popUpTo(Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Profile.route,
        ) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutSuccess = {
                    navController.navigate(Login.route) {
                        popUpTo(Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Classes.route
        ){
            ClassesScreen(
                onOkClick = { navController.popBackStack() }
            )
        }

        composable(
            route = AttendanceRoute.route
        ) {
            AttendanceScreen(
                onBackClick = {
                    navController.popBackStack(
                        route = Dashboard.route,
                        inclusive = false
                    )
                },

                onSubjectClick = { subjectId ->
                    navController.navigate(SubjectAttendanceScreenRoute.createRoute(subjectId))
                },
                onNavigateToQrScanner = {
                    navController.navigate(StudentQrScannerRoute.route)
                },
                onNavigateToQrGenerator = {
                    navController.navigate(TeacherQrGeneratorRoute.route)
                }
            )
        }

        composable(
            route = SubjectAttendanceScreenRoute.routeWithArgs,
            arguments = SubjectAttendanceScreenRoute.arguments
        ) {
            SubjectAttendanceScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = StudentQrScannerRoute.route
        ) {
            StudentQrScannerScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = TeacherQrGeneratorRoute.route
        ) {
            TeacherQrGeneratorScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }


        composable(
            route = AssignmentsRoute.route
        ) {
            AssignmentScreen(
                onOkClick = {
                    navController.popBackStack(
                        route = Dashboard.route,
                        inclusive = false
                    )
                }
            )
        }
    }
}
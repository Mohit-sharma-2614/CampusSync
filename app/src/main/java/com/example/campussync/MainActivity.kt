package com.example.campussync

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresExtension
import androidx.navigation.compose.rememberNavController
import com.example.campussync.persentation.auth.AuthScreen
import com.example.campussync.ui.theme.CampusSyncTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            CampusSyncTheme {
                val viewModel: CampusSyncViewModel = koinViewModel()
                val navController = rememberNavController()

                RootNavigation(
                    viewModel = viewModel,
                    navController = navController
                )

                AuthScreen(
                    navController = rememberNavController()
                )
            }
        }
    }
}

package com.example.campussync.ui.screens.splash

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.campussync.R

@Composable
fun SplashScreen(
    navController: NavHostController,
    onSplashComplete: () -> Unit,
    viewModel: SplashViewModel = viewModel()
) {
    val event by viewModel.event.collectAsState()

    LaunchedEffect(event) {
        if (event != null) {
            onSplashComplete()
            viewModel.clearEvent()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person4,
                contentDescription = "CampusSync Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "CampusSync",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            Text(
                text = "Making Class Life Smarter ✨",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview(showSystemUi = true)
fun SplashScreenPreview() {
    MaterialTheme {
        Surface {
            SplashScreen(
                viewModel = SplashViewModel(),
                onSplashComplete = {},
                navController = rememberNavController()
            )
        }
    }
}
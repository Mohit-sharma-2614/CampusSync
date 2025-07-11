package com.example.campussync.persentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.campussync.navigation.Dashboard
import com.example.campussync.navigation.Login
import com.example.campussync.persentation.components.RichSnackbarComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class) // For FilterChip
@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel() // Injecting the ViewModel
) {
    // Collect the UI state from the ViewModel
    val uiState = viewModel.loginState.collectAsState().value

    // State for password visibility toggle
    var showPassword by remember { mutableStateOf(false) }

    // State for Snackbar message and error status
    val snackbarHostState = remember { SnackbarHostState() }
    var isErrorSnackbar by remember { mutableStateOf(false) } // Renamed to avoid confusion with uiState.errorMessage

    // Destructure properties from uiState for easier access
    val email = uiState.email
    val password = uiState.password
    val isTeacherLoginAttempt = uiState.isTeacherLoginAttempt // Use the new name

    // Observe LoginEvent for one-time events like success or error messages
    LaunchedEffect(Unit) {
        viewModel.loginEvents.collectLatest { event ->
            when (event) {
                is LoginViewModel.LoginEvent.Error -> {
                    isErrorSnackbar = true
                    snackbarHostState.showSnackbar(event.message) // Show the actual error message
                }
                is LoginViewModel.LoginEvent.Success -> {
                    // This event is triggered after successful login and user info persistence
                    isErrorSnackbar = false
                    snackbarHostState.showSnackbar("Login Successful 🎉")
                    // Navigate to Dashboard after successful login and user info saved
                    navController.navigate(Dashboard.route) {
                        popUpTo(Login.route) { inclusive = true } // Clear back stack up to Login
                    }
                }
                is LoginViewModel.LoginEvent.TokenExpired -> {
                    isErrorSnackbar = true
                    snackbarHostState.showSnackbar("Session expired. Please log in again.") // "Session expired. Please log in again."
                    // If you have a dedicated re-authentication flow, you might navigate there.
                    // For now, it will just show the message and user will be logged out.
                }
            }
        }
    }

    // --- UI Structure ---
    Scaffold(
        topBar = {
            // Show a CircularProgressIndicator in the top bar when loading
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        },
        snackbarHost = {
            // Snackbar host to display messages
            SnackbarHost(snackbarHostState) { data ->
                RichSnackbarComponent(
                    message = data.visuals.message,
                    isError = isErrorSnackbar, // Use the state managed by LaunchedEffect
                    onActionClick = {
                        // Dismiss the snackbar and retry login if it was an error
                        data.dismiss() // Dismiss the current snackbar
                        if (isErrorSnackbar) {
                            viewModel.retryLogin()
                        }
                    },
                    // Label for the snackbar action button
                    actionLabel = if (isErrorSnackbar) "Retry" else "OK"
                )
            }
        }
    ) { innerPadding ->
        // Main content box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                // Ensure the column takes full width and padding
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Screen Title
                        Text(
                            text = "Welcome Back!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Subtitle
                        Text(
                            text = "Sign in to your account",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Role Selection (Student/Teacher)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Student Filter Chip
                            FilterChip(
                                selected = !isTeacherLoginAttempt, // Select if not teacher attempt
                                onClick = { viewModel.updateLoginAttemptRole(false) }, // Update role for login attempt
                                label = { Text("Student") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Student") },
                                modifier = Modifier.weight(1f)
                            )

                            // Teacher Filter Chip
                            FilterChip(
                                selected = isTeacherLoginAttempt, // Select if teacher attempt
                                onClick = { viewModel.updateLoginAttemptRole(true) }, // Update role for login attempt
                                label = { Text("Teacher") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Teacher") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email Input Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { viewModel.updateEmail(it) },
                            label = { Text("Email") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email TextField") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = uiState.errorMessage != null && !email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password Input Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { viewModel.updatePassword(it) },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password icon") },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (showPassword) "Hide password" else "Show password"
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = uiState.errorMessage != null && password.length < 6
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Basic local validation for email and password format
                        val isEmailValid = email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
                        val isPasswordValid = password.length >= 6

                        // Sign In Button
                        Button(
                            onClick = {
                                viewModel.onLoginClick() // Trigger login logic in ViewModel
                            },
                            modifier = Modifier.fillMaxWidth(),
                            // Button is enabled only if inputs are valid and not currently loading
                            enabled = isEmailValid && isPasswordValid && !uiState.isLoading
                        ) {
                            Text("Sign In")
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Local validation error messages for UI feedback
                        if (!isPasswordValid || !isEmailValid) {
                            Text(
                                text = if (!isPasswordValid) "Password must be at least 6 characters" else "Email is invalid",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


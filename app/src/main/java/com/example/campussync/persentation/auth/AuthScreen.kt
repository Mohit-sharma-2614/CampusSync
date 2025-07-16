package com.example.campussync.persentation.auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.campussync.navigation.Dashboard
import com.example.campussync.navigation.Login
import com.example.campussync.persentation.components.RichSnackbarComponent
import com.example.campussync.utils.ConnectivityObserver
import compose.icons.AllIcons
import compose.icons.FontAwesomeIcons
import compose.icons.Octicons
import compose.icons.fontawesomeicons.AllIcons
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.Solid
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState = viewModel.loginState.collectAsState().value
    val connectivityStatus by viewModel.connectivityStatus.collectAsState()
    var showPassword by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var isErrorSnackbar by remember { mutableStateOf(false) }

    // Destructure uiState
    val email = uiState.email
    val password = uiState.password
    val isTeacherLoginAttempt = uiState.isTeacherLoginAttempt

    // Animations for card entrance
    val cardScale by animateFloatAsState(
        targetValue = if (uiState.isLoading) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "cardScale"
    )

    // Show snackbar for connectivity
    LaunchedEffect(connectivityStatus) {
        if (connectivityStatus == ConnectivityObserver.Status.Disconnected) {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                message = "Internet is turned off. Please check your connection.",
                duration = SnackbarDuration.Indefinite
            )
        } else {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    // Handle login events
    LaunchedEffect(Unit) {
        viewModel.loginEvents.collectLatest { event ->
            when (event) {
                is LoginViewModel.LoginEvent.Error -> {
                    isErrorSnackbar = true
                    snackbarHostState.showSnackbar(event.message)
                }

                is LoginViewModel.LoginEvent.Success -> {
                    isErrorSnackbar = false
                    navController.navigate(Dashboard.route) {
                        popUpTo(Login.route) { inclusive = true }
                    }

                    // Show snackbar after navigation (optional)
                    launch {
                        snackbarHostState.showSnackbar("Login Successful 🎉")
                    }
                }

                is LoginViewModel.LoginEvent.TokenExpired -> {
                    isErrorSnackbar = true
                    snackbarHostState.showSnackbar("Session expired. Please log in again.")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
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
            SnackbarHost(snackbarHostState) { data ->
                RichSnackbarComponent(
                    message = data.visuals.message,
                    isError = isErrorSnackbar,
                    onActionClick = {
                        data.dismiss()
                        if (isErrorSnackbar) {
                            viewModel.retryLogin()
                        }
                    },
                    actionLabel = if (isErrorSnackbar) "Retry" else "OK",
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .scale(cardScale),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f)
                                )
                            )
                        )
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Animated Title
                        Text(
                            text = "Welcome Back!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Sign in to your account",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Role Selection with Animation
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            RoleFilterChip(
                                label = "Student",
                                icon = Icons.Rounded.Person,
                                selected = !isTeacherLoginAttempt,
                                onClick = { viewModel.updateLoginAttemptRole(false) }
                            )
                            RoleFilterChip(
                                label = "Teacher",
                                icon = Icons.Default.Person2,
                                selected = isTeacherLoginAttempt,
                                onClick = { viewModel.updateLoginAttemptRole(true) }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email Input with Focus Animation
                        AnimatedTextField(
                            value = email,
                            onValueChange = { viewModel.updateEmail(it) },
                            label = "Email",
                            icon = Icons.Default.Email,
                            isError = uiState.errorMessage != null && !email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
                        )
                        val isEmailValid = email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
                        if(!isEmailValid){
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Email is invalid",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp),
                                textAlign = TextAlign.Start
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password Input with Focus Animation
                        AnimatedTextField(
                            value = password,
                            onValueChange = { viewModel.updatePassword(it) },
                            label = "Password",
                            icon = Icons.Default.Lock,
                            isError = uiState.errorMessage != null && password.length < 6,
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (showPassword) "Hide password" else "Show password",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        )
                        // Validation
                        val isPasswordValid = password.length >= 6

                        if(!isPasswordValid){
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Password must be at least 6 characters",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp),
                                textAlign = TextAlign.Start
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Animated Sign In Button
                        val buttonScale by animateFloatAsState(
                            targetValue = if (isEmailValid && isPasswordValid && !uiState.isLoading) 1f else 0.95f,
                            animationSpec = tween(durationMillis = 800),
                            label = "buttonScale"
                        )
                        Button(
                            onClick = { viewModel.onLoginClick() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(buttonScale),
                            enabled = isEmailValid && isPasswordValid && !uiState.isLoading && !uiState.isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text("Sign In")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleFilterChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box {
        val scale by animateFloatAsState(
            targetValue = if (selected) 1f else 0.9f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
            label = "chipScale"
        )
        val backgroundColor by animateColorAsState(
            targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
            animationSpec = tween(durationMillis = 300),
            label = "chipBackground"
        )
        FilterChip(
            selected = selected,
            onClick = onClick,
            label = {
                Text(
                    label,
                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            modifier = Modifier
                .scale(scale),
            colors = FilterChipDefaults.filterChipColors(
                containerColor = backgroundColor,
                selectedContainerColor = backgroundColor
            ),
            border = FilterChipDefaults.filterChipBorder(
                borderColor = MaterialTheme.colorScheme.outline,
                selectedBorderColor = MaterialTheme.colorScheme.primary,
                selectedBorderWidth = 1.dp,
                selected = selected,
                enabled = true
            )
        )
    }
}

@Composable
private fun AnimatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isError: Boolean,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val focusRequester = remember { FocusRequester() }
    val focusState = remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        targetValue = if (isError) MaterialTheme.colorScheme.error else if (focusState.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        animationSpec = tween(durationMillis = 200),
        label = "borderColor"
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                color = if (focusState.value || value.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = "$label TextField",
                tint = if (focusState.value || isError) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { focusState.value = it.isFocused },
        singleLine = true,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor,
            errorBorderColor = borderColor,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Preview
@Composable
fun AuthScreenPreview() {
    AuthScreen(
        navController = rememberNavController(),
        viewModel = hiltViewModel()
    )
}


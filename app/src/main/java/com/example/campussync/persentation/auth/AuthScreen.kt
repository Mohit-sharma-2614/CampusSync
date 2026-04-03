package com.example.campussync.persentation.auth

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import com.example.campussync.data.entity.state.UiState
import com.example.campussync.persentation.components.RichSnackbarComponent
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel = koinViewModel()
) {
    val authState = viewModel.appState.collectAsState().value.session
    val networkState = viewModel.appState.collectAsState().value.network

    val uiDataState = viewModel.uiData.collectAsState().value
    val uiState = viewModel.uiState.collectAsState().value


    val snackbarHostState = remember { SnackbarHostState() }
    var isErrorSnackbar by remember { mutableStateOf(false) }

    // Destructure uiState
    val email = uiDataState.loginCreds.email
    val password = uiDataState.loginCreds.password
    val isTeacherLoginAttempt = uiDataState.loginCreds.isTeacherLoginAttempt

    AuthContent(
        email = email,
        onEmailChange = { viewModel.onEmailChange(it) },
        password = password,
        onPasswordChange = { viewModel.onPasswordChange(it) },
        isTeacherLoginAttempt = isTeacherLoginAttempt,
        userRoleChange = { viewModel.toggleLoginType(it) },
        onLoginClick = { viewModel.login() },
        uiState = uiState,
        credError = uiDataState.credsError,
        isErrorSnackbar = isErrorSnackbar,
        snackbarHostState = snackbarHostState,
    )

}

@Composable
fun AuthContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isTeacherLoginAttempt: Boolean,
    userRoleChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    uiState: UiState,
    credError: CredsError,
    isErrorSnackbar: Boolean,
    snackbarHostState: SnackbarHostState,
){
    // Animations for card entrance
    val cardScale by animateFloatAsState(
        targetValue = if (uiState is UiState.Success) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "cardScale"
    )
    var showPassword by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 48.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator(
//                    modifier = Modifier.align(Alignment.Center),
//                    color = MaterialTheme.colorScheme.primary,
//                    strokeWidth = 2.dp,
//                    trackColor = MaterialTheme.colorScheme.surfaceVariant
//                )
//            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                RichSnackbarComponent(
                    message = data.visuals.message,
                    isError = isErrorSnackbar,
                    onActionClick = {
                        data.dismiss()
//                        if (isErrorSnackbar) {
//                            viewModel.retryLogin()
//                        }
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
                                onClick = { userRoleChange(!isTeacherLoginAttempt) }
                            )
                            RoleFilterChip(
                                label = "Teacher",
                                icon = Icons.Default.Person2,
                                selected = isTeacherLoginAttempt,
                                onClick = { userRoleChange(!isTeacherLoginAttempt) }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email Input with Focus Animation
                        AnimatedTextField(
                            value = email,
                            onValueChange = { onEmailChange(it) },
                            label = "Email",
                            icon = Icons.Default.Email,
                            isError = !credError.emailError.isNullOrEmpty(),
                            errorMessage = credError.emailError ?: ""
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password Input with Focus Animation
                        AnimatedTextField(
                            value = password,
                            onValueChange = { onPasswordChange(it) },
                            label = "Password",
                            icon = Icons.Default.Lock,
                            isError = !credError.passwordError.isNullOrEmpty(),
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (showPassword) "Hide password" else "Show password",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            errorMessage = credError.passwordError ?: ""
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Animated Sign In Button
                        val buttonScale by animateFloatAsState(
                            targetValue = if (credError.passwordError == "" && credError.emailError == "") 1f else 0.95f,
                            animationSpec = tween(durationMillis = 800),
                            label = "buttonScale"
                        )
                        Button(
                            onClick = { onLoginClick() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(buttonScale),
                            enabled = true,
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
    isError: Boolean = false,
    errorMessage: String = "",
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

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
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
        AnimatedVisibility(isError) {
            ErrorMessage(errorMessage)
        }
    }
}

@Composable
fun ErrorMessage(
    message: String,
){
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
        textAlign = TextAlign.Start
    )
}

@Preview
@Composable
fun AuthScreenPreview() {
    AuthContent(
        email = "",
        onEmailChange = {},
        password = "",
        onPasswordChange = {},
        isTeacherLoginAttempt = true,
        userRoleChange = {},
        onLoginClick = {},
        uiState = UiState.Loading,
        isErrorSnackbar = false,
        snackbarHostState = SnackbarHostState(),
        credError = CredsError(
            emailError = null,
            passwordError = null
        ),
    )
}


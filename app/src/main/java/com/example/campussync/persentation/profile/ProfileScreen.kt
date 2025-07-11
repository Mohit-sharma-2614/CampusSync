package com.example.campussync.persentation.profile

import androidx.compose.material3.Text
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.campussync.persentation.components.LogoutConfirmationDialog

// Assuming you have a LogoutConfirmationDialog composable
// fun LogoutConfirmationDialog(onDismiss: () -> Unit, onConfirmLogout: () -> Unit) { ... }

@Composable
fun ProfileScreen(){
    Text("This is profile")
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProfileScreen(
//    onBackClick: () -> Unit,
//    onLogoutSuccess: () -> Unit, // Callback to navigate after logout
//    viewModel: ProfileViewModel = hiltViewModel()
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
//    val snackbarHostState = remember { SnackbarHostState() }
//    var showLogoutDialog by remember { mutableStateOf(false) }
//
//    // Observe UI state for error messages
//    LaunchedEffect(uiState.errorMessage) {
//        uiState.errorMessage?.let { message ->
//            snackbarHostState.showSnackbar(message)
//            // Optionally, clear the error message in ViewModel after showing
//            // viewModel.clearErrorMessage() // Add this function to ViewModel if needed
//        }
//    }
//
//    // Observe isLoggedIn to trigger navigation on logout
//    LaunchedEffect(uiState.isLoggedIn) {
//        if (!uiState.isLoggedIn) {
//            onLogoutSuccess() // Navigate away if user is no longer logged in
//        }
//    }
//
//    // Logout confirmation dialog
//    if (showLogoutDialog) {
//        LogoutConfirmationDialog(
//            onDismiss = { showLogoutDialog = false },
//            onConfirmLogout = {
//                viewModel.onLogout()
//                showLogoutDialog = false
//                // Navigation will be handled by LaunchedEffect(uiState.isLoggedIn)
//            }
//        )
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Profile", maxLines = 1, overflow = TextOverflow.Ellipsis) },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { showLogoutDialog = true }) {
//                        Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = "Logout")
//                    }
//                },
//                scrollBehavior = scrollBehavior
//            )
//        },
//        snackbarHost = { SnackbarHost(snackbarHostState) }
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//            // Loading state
//            if (uiState.isLoading) {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//            }
//            // Error state or not logged in
//            else if (uiState.errorMessage != null || !uiState.isLoggedIn) {
//                Column(
//                    modifier = Modifier.fillMaxSize().padding(16.dp),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = uiState.errorMessage ?: "You are not logged in.",
//                        color = MaterialTheme.colorScheme.error,
//                        style = MaterialTheme.typography.bodyLarge
//                    )
//                    Spacer(Modifier.height(16.dp))
//                    // Optionally, a button to navigate to login if not logged in
//                    // Button(onClick = onLogoutSuccess) { Text("Go to Login") }
//                }
//            }
//            // Profile content
//            else {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .verticalScroll(rememberScrollState()) // Enable scrolling for long content
//                        .padding(16.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    // --- Profile Header Section ---
//                    ProfileHeader(
//                        name = uiState.name,
//                        role = uiState.role,
//                        id = uiState.id.toString()
//                    )
//
//                    Spacer(Modifier.height(24.dp))
//
//                    // --- Contact Information Card ---
//                    ProfileInfoCard(
//                        title = "Contact Information",
//                        icon = Icons.Default.Email,
//                        items = listOf(
//                            "Email" to uiState.email
//                        )
//                    )
//
//                    Spacer(Modifier.height(16.dp))
//
//                    // --- Role-Specific Details Card ---
//                    if (uiState.role == "Student") {
//                        ProfileInfoCard(
//                            title = "Academic Details",
//                            icon = Icons.Default.School,
//                            items = listOfNotNull(
//                                "Student ID" to uiState.id.toString(),
//                                uiState.studentUid?.let { "Student UID" to it },
//                                uiState.semester?.let { "Semester" to it.toString() }
//                            )
//                        )
//                    } else if (uiState.role == "Teacher") {
//                        ProfileInfoCard(
//                            title = "Professional Details",
//                            icon = Icons.Default.Work,
//                            items = listOfNotNull(
//                                "Teacher ID" to uiState.id.toString(),
//                                uiState.department?.let { "Department" to it }
//                            )
//                        )
//                    }
//
//                    Spacer(Modifier.height(24.dp))
//
//                    // --- Logout Button (can also be in TopAppBar) ---
//                    Button(
//                        onClick = { showLogoutDialog = true },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(56.dp),
//                        shape = RoundedCornerShape(12.dp),
//                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
//                    ) {
//                        Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = "Logout")
//                        Spacer(Modifier.width(8.dp))
//                        Text("Logout")
//                    }
//                }
//            }
//        }
//    }
//}
//
//// --- Reusable Profile Composable Sections ---
//
//@Composable
//private fun ProfileHeader(name: String, role: String, id: String) {
//    val gradient = Brush.linearGradient(
//        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
//    )
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(24.dp))
//            .background(gradient)
//            .padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // Profile Picture Placeholder
//        Box(
//            modifier = Modifier
//                .size(100.dp)
//                .clip(CircleShape)
//                .background(Color.White.copy(alpha = 0.2f)),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = name.firstOrNull()?.uppercase() ?: "",
//                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
//                color = Color.White
//            )
//        }
//
//        Spacer(Modifier.height(16.dp))
//
//        Text(
//            text = name,
//            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
//            color = Color.White,
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis
//        )
//        Text(
//            text = "$role (ID: $id)",
//            style = MaterialTheme.typography.bodyLarge,
//            color = Color.White.copy(alpha = 0.8f)
//        )
//    }
//}
//
//@Composable
//private fun ProfileInfoCard(
//    title: String,
//    icon: ImageVector,
//    items: List<Pair<String, String>>
//) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Column(modifier = Modifier.padding(20.dp)) {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(
//                    imageVector = icon,
//                    contentDescription = null,
//                    tint = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.size(24.dp)
//                )
//                Spacer(Modifier.width(12.dp))
//                Text(
//                    text = title,
//                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
//                    color = MaterialTheme.colorScheme.onSurface
//                )
//            }
//            Spacer(Modifier.height(16.dp))
//            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                items.forEach { (label, value) ->
//                    ProfileInfoRow(label = label, value = value)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun ProfileInfoRow(label: String, value: String) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = "$label:",
//            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
//            color = MaterialTheme.colorScheme.onSurfaceVariant,
//            modifier = Modifier.width(100.dp) // Fixed width for labels
//        )
//        Spacer(Modifier.width(8.dp))
//        Text(
//            text = value,
//            style = MaterialTheme.typography.bodyLarge,
//            color = MaterialTheme.colorScheme.onSurface,
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis
//        )
//    }
//}
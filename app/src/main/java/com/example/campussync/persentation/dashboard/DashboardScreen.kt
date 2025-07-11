package com.example.campussync.persentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.campussync.persentation.components.LogoutConfirmationDialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onCardClick: (DashboardCard) -> Unit,
    onLogOutClick: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Show logout confirmation dialog
    if (showDialog) {
        LogoutConfirmationDialog(
            onDismiss = { showDialog = false },
            onConfirmLogout = {
                viewModel.onLogout()
                showDialog = false
                onLogOutClick() // Navigate away after successful logout
            }
        )
    }

    // Observe error messages from the ViewModel and show a Snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            // Optionally, clear the error message in the ViewModel after displaying it
            // viewModel.clearErrorMessage() // You'd need to add this to DashboardViewModel
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("CampusSync", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    // Consider adding actual navigation drawer logic here if 'Menu' implies it
                    IconButton(onClick = { /* TODO: Open Navigation Drawer */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) } // Integrate SnackbarHost
    ) { padding ->
        // Handle loading state
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        // Handle error state (if not already handled by SnackbarHost)
        else if (uiState.errorMessage != null && uiState.cards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error loading data: ${uiState.errorMessage}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    // Optional: Add a retry button
                    // Button(onClick = { viewModel.fetchDashboardSummaryData() }) {
                    //     Text("Retry")
                    // }
                }
            }
        }
        // Display content when loaded and no critical errors preventing display
        else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(padding)
            ) {
                if (uiState.cards.isEmpty()) {
                    // Show a message if no cards are available (e.g., after initial load, or if teacher has no data)
                    item {
                        Text(
                            text = "No dashboard items available.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(uiState.cards) { card ->
                        DashboardCard(
                            card = card,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onCardClick(card) } // Pass the whole card object
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardCard(
    card: DashboardCard,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    val gradient = Brush.linearGradient(colors = card.colors)

    Card(
        onClick = onClick,
        modifier = modifier.heightIn(min = 140.dp),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent) // Card itself is transparent
    ) {
        Box(
            modifier = Modifier
                .background(gradient, shape = shape) // Apply background with shape to the Box
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = card.iconRes,
                    contentDescription = null, // Content description for icons should be provided if meaningful
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            card.badge?.let { value ->
                // Ensure badge is only shown if value > 0
                if (value > 0) {
                    Badge(
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = Color.White,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(value.toString())
                    }
                }
            }

            card.extra?.let { extra ->
                Text(
                    text = extra,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }
    }
}

//@Composable
//fun LogoutConfirmationDialog(
//    onDismiss: () -> Unit,
//    onConfirmLogout: () -> Unit
//) {
//    // Implement your dialog here
//    // Example:
//    // AlertDialog(
//    //     onDismissRequest = onDismiss,
//    //     title = { Text("Logout") },
//    //     text = { Text("Are you sure you want to log out?") },
//    //     confirmButton = {
//    //         Button(onClick = onConfirmLogout) { Text("Confirm") }
//    //     },
//    //     dismissButton = {
//    //         Button(onClick = onDismiss) { Text("Cancel") }
//    //     }
//    // )
//}
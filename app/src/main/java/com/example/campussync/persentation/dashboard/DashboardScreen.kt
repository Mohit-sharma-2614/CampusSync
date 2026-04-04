package com.example.campussync.persentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onCardClick: (DashboardCard) -> Unit = { },
    onLogOutClick: () -> Unit = { },
    onNavigateToLoginScreen: () -> Unit = { },
    viewModel: DashboardViewModel = koinViewModel()
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Dashboard")
        Spacer(
            modifier = Modifier.size(16.dp)
        )
        Button(onClick = { viewModel.logOut() }) {
            Text(text = "Log Out", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
        }
    }

//    val uiState by viewModel.uiState.collectAsState()
//    var showDialog by remember { mutableStateOf(false) }
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    val scale = AnimatedScaleOnDataLoad(!uiState.cards.isEmpty())
//
//    // Show logout confirmation dialog
//    if (showDialog) {
//        LogoutConfirmationDialog(
//            onDismiss = { showDialog = false },
//            onConfirmLogout = {
//                viewModel.logout()
//                showDialog = false
//                onLogOutClick() // Navigate away after successful logout
//            }
//        )
//    }
//    if (!uiState.isLoggedIn) {
//        onNavigateToLoginScreen()
//    }
//
//    // Show snackbar for internet connectivity
//    LaunchedEffect(connectivityStatus) {
//        if (connectivityStatus == ConnectivityObserver.Status.Disconnected) {
//            snackbarHostState.currentSnackbarData?.dismiss() // Dismiss any existing snackbar
//            snackbarHostState.showSnackbar(
//                message = "Internet is turned off. Please check your connection.",
//                duration = SnackbarDuration.Indefinite // Keep visible until reconnected
//            )
//        } else {
//            snackbarHostState.currentSnackbarData?.dismiss() // Dismiss when reconnected
//        }
//    }
//
//    // Observe error messages from the ViewModel and show a Snackbar
//    LaunchedEffect(uiState.errorMessage) {
//        uiState.errorMessage?.let { message ->
//            snackbarHostState.showSnackbar(message)
//            // Optionally, clear the error message in the ViewModel after displaying it
//            // viewModel.clearErrorMessage() // You'd need to add this to DashboardViewModel
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = {
//                    Text(
//                        "CampusSync",
//                        fontWeight = FontWeight.Bold,
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//                },
////                navigationIcon = {
////                    // Consider adding actual navigation drawer logic here if 'Menu' implies it
////                    IconButton(onClick = { /* TODO: Open Navigation Drawer */ }) {
////                        Icon(
////                            Icons.Default.Menu,
////                            contentDescription = "Menu",
////                            tint = MaterialTheme.colorScheme.onSurfaceVariant
////                        )
////                    }
////                },
//                actions = {
//                    IconButton(onClick = { showDialog = true }) {
//                        Icon(
//                            Icons.AutoMirrored.Rounded.Logout,
//                            contentDescription = "Logout",
//                            tint = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.surface,
//                    titleContentColor = MaterialTheme.colorScheme.onSurface
//                )
//            )
//        },
//        snackbarHost = { SnackbarHost(snackbarHostState) }, // Integrate SnackbarHost
//        containerColor = MaterialTheme.colorScheme.background
//    ) { padding ->
//        // Handle loading state
//        if (uiState.isLoading) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(padding),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator(
//                    color = MaterialTheme.colorScheme.primary
//                )
//            }
//        }
//        // Handle error state (if not already handled by SnackbarHost)
//        else if (uiState.errorMessage != null && uiState.cards.isEmpty()) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(padding),
//                contentAlignment = Alignment.Center
//            ) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Text(
//                        text = "Error loading data: ${uiState.errorMessage}",
//                        color = MaterialTheme.colorScheme.error,
//                        style = MaterialTheme.typography.bodyLarge
//                    )
//                    // Optional: Add a retry button
//                    // Button(onClick = { viewModel.fetchDashboardSummaryData() }) {
//                    //     Text("Retry")
//                    // }
//                }
//            }
//        }
//        // Display content when loaded and no critical errors preventing display
//        else {
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                contentPadding = PaddingValues(
//                    start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp
//                ),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//                horizontalArrangement = Arrangement.spacedBy(16.dp),
//                modifier = Modifier.padding(padding)
//            ) {
//                if (uiState.cards.isEmpty()) {
//                    // Show a message if no cards are available (e.g., after initial load, or if teacher has no data)
//                    item {
//                        Text(
//                            text = "No dashboard items available.",
//                            style = MaterialTheme.typography.bodyLarge,
//                            color = MaterialTheme.colorScheme.onSurfaceVariant,
//                            modifier = Modifier.padding(16.dp)
//                        )
//                    }
//                } else {
//
//                    items(uiState.cards) { card ->
//                        DashboardCard(
//                            card = card,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .scale(scale),
//                            onClick = { onCardClick(card) } // Pass the whole card object
//                        )
//                    }
//                }
//            }
//        }
//    }
}

@Composable
private fun DashboardCard(
    card: DashboardCard,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    // Use Material Design 3 color tokens for the gradient
    val gradient = Brush.linearGradient(
        colors = card.colors
    )

    Card(
        onClick = onClick,
        modifier = modifier.heightIn(min = 140.dp),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Box(
            modifier = Modifier
                .background(gradient, shape = shape)
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = card.iconRes,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            card.badge?.let { value ->
                // Ensure badge is only shown if value > 0
                if (value > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.2f),
                        contentColor = MaterialTheme.colorScheme.onSurface,
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
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }
    }
}


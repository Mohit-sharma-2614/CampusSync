package com.example.campussync.persentation.dashboard

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.campussync.persentation.components.AnimatedScaleOnDataLoad
import com.example.campussync.persentation.components.LogoutConfirmationDialog
import com.example.campussync.utils.config.TopBarAction
import com.example.campussync.utils.config.TopBarConfig
import com.example.campussync.utils.views.AppLoading
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    setTopBar: (TopBarConfig) -> Unit,
    onCardClick: (DashboardCard) -> Unit = { },
    onLogOutClick: () -> Unit = { },
    viewModel: DashboardViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scale = AnimatedScaleOnDataLoad(!uiState.cards.isEmpty())

    val logOutAction = TopBarAction(
        icon = Icons.AutoMirrored.Rounded.Logout,
        onClick = { showDialog = true },
        contentDescription = "Log out button"
    )

    LaunchedEffect(Unit) {
        setTopBar(
            TopBarConfig(
                title = "Dashboard",
                actions = listOf(logOutAction)
            )
        )
    }

    // Blur effect for Loading.
    Box{
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(0.98f)
        ) {
            AnimatedVisibility(showDialog) {
                if(showDialog){
                    LogoutConfirmationDialog(
                        onDismiss = { showDialog = false },
                        onConfirmLogout = {
                            viewModel.logOut()
                            showDialog = false
                            // onLogOutClick() // Navigate away after successful logout
                        }
                    )
                }
            }

            // Observe error messages from the ViewModel and show a Snackbar
            LaunchedEffect(uiState.errorMessage) {
                uiState.errorMessage?.let { message ->
                    snackbarHostState.showSnackbar(message)
                    // Optionally, clear the error message in the ViewModel after displaying it
                    // viewModel.clearErrorMessage() // You'd need to add this to DashboardViewModel
                }
            }

            DashboardContent(
                onCardClick = onCardClick,
                cards = uiState.cards,
                scale = scale
            )
        }

        if(uiState.isLoading){
            AppLoading(
                message = "Loading...",
                isFullScreen = true,
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardContent(
    onCardClick: (DashboardCard) -> Unit,
    cards: List<DashboardCard>,
    scale: Float
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        if (cards.isEmpty()) {
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

            items(
                cards,
                key = { it.title }
            ) {card ->
                DashboardCard(
                    card = card,
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale),
                    onClick = { onCardClick(card) } // Pass the whole card object
                )
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


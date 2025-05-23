package com.example.campussync.ui.screens.classes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.campussync.ui.AppViewModelProvider
import com.example.campussync.ui.screens.utils.SmallTopAppBar

/**
 * A vibrant schedule screen showing classes for the selected day.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassesScreen(
//    uiState: ClassesUiState = remember { demoState },
    onBackClick: () -> Unit = {},
    onClassClick: (ClassSession) -> Unit = {},
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ClassesViewModel = viewModel( factory = AppViewModelProvider.Factory )
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedIndex = uiState.selectedDayIndex
    val selectedNavIndex = uiState.selectedNavIndex

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Your Classes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            )
        },
        bottomBar = {
            NavigationBar {
                uiState.navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == selectedNavIndex,
                        onClick = {
                            viewModel.onNavItemSelected(index)
                            navController.navigate(item.label)
                        },
                        icon = { Icon(item.icon, null) },
                        label = { Text(item.label) }
                    )
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            /* ---------- Days row ---------- */
            Row(
                Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.days.forEachIndexed { index, day ->
                    FilterChip(
                        selected = index == selectedIndex,
                        onClick = { viewModel.onDaySelected(index) },
                        label = {
                            Text(day.label, maxLines = 1)
                        },
                        leadingIcon = {
                            AnimatedVisibility(
                                visible = index == selectedIndex,
                                enter = fadeIn(), exit = fadeOut()
                            ) {
                                Box(
                                    Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    )
                }
            }

            /* ---------- Class list ---------- */
            val day = uiState.days[selectedIndex]
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(day.sessions.size) { i ->
                    val session = day.sessions[i]
                    ClassCard(session, onClick = { onClassClick(session) })
                }
            }
        }
    }
}

@Composable
private fun ClassCard(
    session: ClassSession,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(session.color.copy(alpha = 0.9f), session.color.copy(alpha = 0.6f))
    )

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            Modifier
                .background(gradient)
                .padding(20.dp)
        ) {
            /* Time column */
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(session.start, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold), color = Color.White)
                HorizontalDivider(
                    Modifier
                        .width(1.dp)
                        .height(12.dp),
                    color = Color.White.copy(alpha = 0.5f)
                )
                Text(session.end, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold), color = Color.White)
            }

            Spacer(Modifier.width(16.dp))

            /* Details column */
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    session.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
                Text(session.lecturer, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                Text(
                    session.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            /* Current indicator */
            if (session.isOngoing) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("Now", style = MaterialTheme.typography.labelMedium, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                    progress = { session.progress },
                    modifier = Modifier
                                                .width(48.dp)
                                                .height(4.dp)
                                                .clip(RoundedCornerShape(2.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f),
                    strokeCap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

/* ---------- PREVIEW ---------- */

@Preview(showBackground = true, device = "id:pixel_7", apiLevel = 35)
@Composable
private fun ClassesScreenPreview() {
    MaterialTheme {
        ClassesScreen(
            onBackClick = {},
            onClassClick = {},
            navController = rememberNavController()
        )
    }
}

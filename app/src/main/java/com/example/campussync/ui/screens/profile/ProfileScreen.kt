package com.example.campussync.ui.screens.profile


import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.campussync.R
import com.example.campussync.ui.AppViewModelProvider

/**
 * Revamped student Profile screen with dynamic timeline, GPA chart, quick actions & notifications.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
//    uiState: ProfileUiState = remember { ProfileUiState },
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    onQuickAction: (QuickAction) -> Unit = {},
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel( factory = AppViewModelProvider.Factory )
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text("Profile", maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            NavigationBar {
                uiState.navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == uiState.selectedNavIndex,
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
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            /* ---------- HEADER ---------- */
            HeaderSection(uiState)

            /* ---------- STATS ---------- */
            StatsSection(uiState)

            /* ---------- QUICK ACTIONS ---------- */
            QuickActionsRow(actions = uiState.quickActions, onClick = onQuickAction)

            /* ---------- ACADEMIC TIMELINE ---------- */
            AcademicTimeline(timeline = uiState.timeline)

            /* ---------- NOTIFICATIONS ---------- */
            NotificationsList(notifications = uiState.notifications)

            /* ---------- LOGOUT ---------- */
            LogoutButton(onLogout)
        }
    }
}

/* -------------------------------------------------- */
/* ------------------- SECTIONS ---------------------- */
/* -------------------------------------------------- */

@Composable
private fun HeaderSection(state: ProfileUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
            .padding(top = 36.dp, bottom = 52.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(state.avatar),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.height(14.dp))
            Text(state.name, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
            Text("${state.branch} • Sem ${state.semester}", color = Color.White.copy(alpha = 0.9f))
            Text("ID: ${state.rollNumber}", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun StatsSection(state: ProfileUiState) {
    Row(
        Modifier
            .padding(horizontal = 16.dp)
            .offset(y = (-36).dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AttendanceGauge(percent = state.attendancePercent)
        StatCard(label = "CGPA", value = "${state.cgpa}")
        StatCard(label = "Credits", value = "${state.creditsEarned}")
    }
}

@Composable
private fun AttendanceGauge(percent: Int) {
    val animatedColor by animateColorAsState(
        targetValue = if (percent >= 75) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.size(96.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                progress = percent / 100f,
                strokeWidth = 6.dp,
                color = animatedColor,
                trackColor = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.2f)
            )
            Text("${percent}%", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.size(width = 96.dp, height = 96.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun QuickActionsRow(actions: List<QuickAction>, onClick: (QuickAction) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(actions) { action ->
            ElevatedAssistChip(
                onClick = { onClick(action) },
                label = { Text(action.label) },
                leadingIcon = {
                    Icon(action.icon, contentDescription = null)
                }
            )
        }
    }
}

@Composable
private fun AcademicTimeline(timeline: List<SemesterRecord>) {
    Column(Modifier.padding(start = 16.dp, top = 8.dp)) {
        Text("Academic Timeline", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        LazyRow(
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(timeline) { record ->
                SemesterCard(record)
            }
        }
    }
}

@Composable
private fun SemesterCard(record: SemesterRecord) {
    Card(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.size(width = 140.dp, height = 100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Sem ${record.semester}", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            Text("GPA ${record.gpa}", style = MaterialTheme.typography.bodySmall)
            LinearProgressIndicator(
                progress = record.creditsEarned / record.creditsTotal.toFloat(),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
private fun NotificationsList(notifications: List<NotificationItem>) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (notifications.isNotEmpty()) {
            Text("Notifications", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            notifications.forEach { note ->
                Surface(
                    tonalElevation = 2.dp,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(note.icon, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(note.title)
                        }
                        Text(note.subtitle, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    OutlinedButton(
        onClick = onLogout,
        modifier = Modifier
//            .align(Alignment.CenterHorizontally)
            .padding(bottom = 48.dp, top = 16.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
    ) {
        Text("Log out")
    }
}

/* -------------------------------------------------- */
/* ------------------ PREVIEW ----------------------- */
/* -------------------------------------------------- */

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun ProfilePreview() {
    MaterialTheme {
        ProfileScreen(
            onBackClick = {},
            onEditClick = {},
            onLogout = {},
            onQuickAction = {},
            navController = rememberNavController()
        )
    }
}

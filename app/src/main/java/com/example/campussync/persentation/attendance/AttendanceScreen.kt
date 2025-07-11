package com.example.campussync.persentation.attendance

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Beautiful attendance tracking screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onBackClick: () -> Unit = {},
    // onFilterChange should ideally be connected to ViewModel
    // We'll update this to call a ViewModel function
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    // Collect the UI state from the ViewModel
    val uiState by viewModel.attendanceUiState.collectAsState()

    // Collect user role and ID directly from ViewModel (which gets it from UserPreferences)
    val isTeacher by viewModel.isTeacher.collectAsState()
    val userId by viewModel.userId.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState() // Not directly used here, but good to know it's available

    // SnackbarHostState for displaying messages
    val snackbarHostState = remember { SnackbarHostState() }

    // Effect to display error messages from the ViewModel
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            // Optionally, clear the error message in the ViewModel after showing it
            // viewModel.clearErrorMessage() // You would need to add this fun to ViewModel
        }
    }

    // Logging to observe the collected values
    LaunchedEffect(userId, isTeacher) {
        Log.d("AttendanceScreen", "Current User ID: $userId, Is Teacher: $isTeacher")
        // If you need to trigger specific actions based on the user's role
        // that are *not* handled by the ViewModel's init block, you could do it here.
        // For example, if you have a "FetchTeacherSpecificData" function in ViewModel:
        // if (isTeacher && userId != null) {
        //     viewModel.fetchTeacherSpecificAttendance(userId.toLong())
        // }
    }


    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Add SnackbarHost
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { padding ->
        // Conditional content based on loading state or error
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.errorMessage != null) {
            // Display error message and potentially a retry button
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Error: ${uiState.errorMessage}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                // You might need a way to retry the data load from the ViewModel
                // For example, if the ViewModel has a `refreshData()` function
                // Button(onClick = { viewModel.refreshData(userId?.toLong() ?: 0L) }) { Text("Retry") }
                // For now, no specific retry button is added since getData is triggered by userId collect
            }
        } else if (userId == null || userId == "0" || (isTeacher && uiState.courses.isEmpty())) {
            // Handle cases where user data isn't available or teacher has no data (if not implemented)
            // You might show a message like "Please log in" or "No attendance data available for your role."
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isTeacher) {
                    Text(
                        text = "Teacher attendance view not yet implemented.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "No user ID found or no attendance data available.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        else {
            // Main content when data is loaded and no error
            Column(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                /* ---------- Overall gauge ---------- */
                OverallGauge(percent = uiState.overallPercent.toInt())

                /* ---------- Filter chips ---------- */
                // Connect onFilterChange to ViewModel's function
                FilterRow(selected = uiState.selectedFilter, onSelect = { filter ->
                    viewModel.updateSelectedFilter(filter)
                    // If filter change triggers data re-fetch, you might call getData again
                    // viewModel.getData(userId.toLong()) // Example, depends on your filter logic
                })

                /* ---------- Course list ---------- */
                if (uiState.courses.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No attendance records found.", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f) // Make LazyColumn fill remaining space
                    ) {
                        items(uiState.courses) { course ->
                            CourseAttendanceCard(course)
                        }
                    }
                }
            }
        }
    }
}

/* ---------------- SECTIONS ---------------- */

@Composable
private fun OverallGauge(percent: Int) {
    val animatedColor by animateColorAsState(
        targetValue = if (percent >= 75) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
        label = "gaugeColorAnimation" // Added label for better debugging
    )

    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { percent / 100f }, // Correct way to pass progress (Float between 0f and 1f)
            modifier = Modifier.size(160.dp),
            color = animatedColor,
            strokeWidth = 10.dp,
            trackColor = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.2f),
            strokeCap = StrokeCap.Round,
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Overall", style = MaterialTheme.typography.bodyMedium)
            Text("$percent%", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterRow(selected: AttendanceFilter, onSelect: (AttendanceFilter) -> Unit) {
    Row(
        Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(), // Make row fill width for consistent spacing
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AttendanceFilter.entries.forEach { filter ->
            FilterChip(
                selected = selected == filter,
                onClick = { onSelect(filter) },
                label = { Text(filter.label) },
                modifier = Modifier.weight(1f) // Distribute chips evenly
            )
        }
    }
}

@Composable
private fun CourseAttendanceCard(item: AttendanceCourseItem) {
    val gradient = Brush.horizontalGradient(listOf(item.color.copy(alpha = 0.9f), item.color.copy(alpha = 0.6f)))

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent), // Use Transparent for background
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .background(gradient)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            /* Percent bubble */
            Box(
                Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("${item.percent}%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(item.courseName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = Color.White)
                Text(item.lecturer, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
                LinearProgressIndicator(
                    progress = { item.percent / 100f }, // Correct way to pass progress (Float between 0f and 1f)
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f),
                    strokeCap = StrokeCap.Round,
                )
            }
        }
    }
}


/* ---------------- MODELS ---------------- */

enum class AttendanceFilter(val label: String) { SEMESTER("Semester"), MONTH("Month"), ALL("All") } // Added ALL for more options

// Existing data class, keep as is
data class AttendanceCourseItem(
    val courseName: String,
    val lecturer: String,
    val percent: Int,
    val color: Color
)
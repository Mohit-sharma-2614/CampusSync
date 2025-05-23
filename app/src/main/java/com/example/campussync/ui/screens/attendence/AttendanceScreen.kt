package com.example.campussync.ui.screens.attendence


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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.campussync.ui.AppViewModelProvider

/**
 * Beautiful attendance tracking screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    uiState: AttendanceUiState = remember { demoUiState },
    onBackClick: () -> Unit = {},
    onFilterChange: (AttendanceFilter) -> Unit = {},
    navController: NavController,
    viewModel: AttendanceViewModel = viewModel( factory = AppViewModelProvider.Factory )
) {
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
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            /* ---------- Overall gauge ---------- */
            OverallGauge(percent = uiState.overallPercent)

            /* ---------- Filter chips ---------- */
            FilterRow(selected = uiState.selectedFilter, onSelect = onFilterChange)

            /* ---------- Course list ---------- */
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.courses) { course ->
                    CourseAttendanceCard(course)
                }
            }
        }
    }
}

/* ---------------- SECTIONS ---------------- */

@Composable
private fun OverallGauge(percent: Int) {
    val animatedColor by animateColorAsState(
        targetValue = if (percent >= 75) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
    )

    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
        progress = { percent / 100f },
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

@Composable
private fun FilterRow(selected: AttendanceFilter, onSelect: (AttendanceFilter) -> Unit) {
    Row(
        Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AttendanceFilter.entries.forEach { filter ->
            FilterChip(
                selected = selected == filter,
                onClick = { onSelect(filter) },
                label = { Text(filter.label) }
            )
        }
    }
}

@Composable
private fun CourseAttendanceCard(item: AttendanceCourseItem) {
    val gradient = Brush.horizontalGradient(listOf(item.color.copy(alpha = 0.9f), item.color.copy(alpha = 0.6f)))

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
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
                progress = { item.percent / 100f },
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

enum class AttendanceFilter(val label: String) { SEMESTER("Semester"), MONTH("Month") }

data class AttendanceCourseItem(
    val courseName: String,
    val lecturer: String,
    val percent: Int,
    val color: Color
)

data class AttendanceUiState(
    val overallPercent: Int,
    val selectedFilter: AttendanceFilter,
    val courses: List<AttendanceCourseItem>
)

/* ---------------- DEMO DATA ---------------- */

private val demoUiState: AttendanceUiState
    get() = AttendanceUiState(
        overallPercent = 82,
        selectedFilter = AttendanceFilter.SEMESTER,
        courses = listOf(
            AttendanceCourseItem("Operating Systems", "Dr. Gupta", 80, Color(0xFF42A5F5)),
            AttendanceCourseItem("DBMS", "Dr. Singh", 90, Color(0xFFAB47BC)),
            AttendanceCourseItem("Android Dev", "Prof. Sharma", 75, Color(0xFF66BB6A)),
            AttendanceCourseItem("Machine Learning", "Dr. Rao", 70, Color(0xFFFF7043))
        )
    )

/* ---------------- PREVIEW ---------------- */

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun AttendancePreview() {
    MaterialTheme {
        AttendanceScreen(
            uiState = demoUiState,
            navController = rememberNavController()
        )
    }
}

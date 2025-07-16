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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.example.campussync.data.model.Subject
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.scale
import com.example.campussync.persentation.components.AnimatedScaleOnDataLoad
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onBackClick: () -> Unit = {},
    onNavigateToQrScanner: () -> Unit,
    onNavigateToQrGenerator: () -> Unit,
    onSubjectClick: (Long) -> Unit,
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.attendanceUiState.collectAsState()
    val isTeacher by viewModel.isTeacher.collectAsState()
    val userId by viewModel.userId.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            // Optionally, clear the error message in the ViewModel after display
            // viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(userId, isTeacher) {
        Log.d("AttendanceScreen", "Current User ID: $userId, Is Teacher: $isTeacher")
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Attendance",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (isTeacher) {
                FloatingActionButton(
                    onClick = onNavigateToQrGenerator,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Filled.Add, "Generate QR Code")
                }
            } else {
                FloatingActionButton(
                    onClick = onNavigateToQrScanner,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Filled.Add, "Scan QR Code")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { padding ->
        // Handle Loading and Error states for both roles
        if (uiState.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (uiState.errorMessage != null) {
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
            }
        } else if (userId == null || userId == "0") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "User not logged in or ID not available. Please log in.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Main content based on user role
            Column(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                if (isTeacher) {
                    // --- Teacher View ---
                    TeacherAttendanceView(
                        teacherSubjects = uiState.subjects,
                        attendanceSummaries = uiState.teacherSubjectAttendanceSummary,
                        onSubjectClick = onSubjectClick
                    )
                } else {
                    // --- Student View ---
                    StudentAttendanceView(
                        overallPercent = uiState.overallPercent,
                        selectedFilter = uiState.selectedFilter,
                        courses = uiState.courses,
                        onFilterSelect = viewModel::updateSelectedFilter,
                        onSubjectClick = onSubjectClick,
                        uiState = uiState
                    )
                }
            }
        }
    }
}

/* ---------------- SECTIONS FOR STUDENT VIEW ---------------- */

@Composable
private fun StudentAttendanceView(
    overallPercent: Double,
    selectedFilter: AttendanceFilter,
    courses: List<AttendanceCourseItem>,
    onFilterSelect: (AttendanceFilter) -> Unit,
    onSubjectClick: (Long) -> Unit,
    uiState: AttendanceUiState
) {
    OverallGauge(percent = overallPercent.toInt())
    FilterRow(selected = selectedFilter, onSelect = onFilterSelect)

    Column {
        if (courses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No attendance records found for your courses.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(courses) { course ->
                    val subjectId = uiState.subjects.find { it.name == course.courseName }?.id ?: 0L
                    CourseAttendanceCard(course, onClick = { onSubjectClick(subjectId) })
                }
            }
        }
    }
}

/* ---------------- SECTIONS FOR TEACHER VIEW ---------------- */

@Composable
private fun TeacherAttendanceView(
    teacherSubjects: List<Subject>,
    attendanceSummaries: Map<Long, TeacherSubjectAttendanceSummary>,
    onSubjectClick: (Long) -> Unit
) {
    Column {
        if (teacherSubjects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No subjects assigned to you. Cannot display attendance summaries.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    "Subjects Attendance Summary",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(teacherSubjects) { subject ->
                        val summary = attendanceSummaries[subject.id]
                        if (summary != null) {
                            TeacherSubjectAttendanceCard(
                                summary,
                                onClick = { onSubjectClick(subject.id) },
                            )
                        } else {
                            TeacherSubjectAttendanceCard(
                                TeacherSubjectAttendanceSummary(
                                    subjectId = subject.id,
                                    subjectName = subject.name,
                                    totalStudents = 0,
                                    presentStudents = 0,
                                    overallClassPercentage = 0.0,
                                    latestAttendanceDate = "No attendance yet"
                                ),
                                onClick = { onSubjectClick(subject.id) },

                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeacherSubjectAttendanceCard(summary: TeacherSubjectAttendanceSummary, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                summary.subjectName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Total Students: ${summary.totalStudents}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Today Present: ${summary.presentStudents}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    summary.latestAttendanceDate?.let {

                        val originalDate = it
                        val parsedDate = LocalDate.parse(originalDate)
                        val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

                        Text(
                            "Last Marked: $formattedDate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    "${summary.overallClassPercentage.toInt()}%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (summary.overallClassPercentage >= 75) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            LinearProgressIndicator(
                progress = { (summary.overallClassPercentage / 100f).toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (summary.overallClassPercentage >= 75) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

/* ---------------- REUSABLE COMPONENTS (ADJUSTED FOR CLICK) ---------------- */

@Composable
private fun OverallGauge(percent: Int) {
    val animatedColor by animateColorAsState(
        targetValue = if (percent >= 75) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
        label = "gaugeColorAnimation"
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
            strokeCap = StrokeCap.Round
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Overall",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "$percent%",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterRow(selected: AttendanceFilter, onSelect: (AttendanceFilter) -> Unit) {
    Row(
        Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AttendanceFilter.entries.forEach { filter ->
            FilterChip(
                selected = selected == filter,
                onClick = { onSelect(filter) },
                label = {
                    Text(
                        filter.toString(),
                        color = if (selected == filter) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
private fun CourseAttendanceCard(item: AttendanceCourseItem, onClick: () -> Unit) {
    val gradient = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
        )
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${item.percent}%",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    item.courseName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    item.lecturer,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )
                LinearProgressIndicator(
                    progress = { item.percent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.onPrimary,
                    trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}
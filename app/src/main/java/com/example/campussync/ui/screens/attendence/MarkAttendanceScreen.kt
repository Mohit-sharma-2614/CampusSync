package com.example.campussync.ui.screens.attendence

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campussync.ui.AppViewModelProvider
import com.example.campussync.ui.screens.utils.SmallTopAppBar
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

/**
 * Screen where a student can mark attendance for the current day.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkAttendanceScreen(
    uiState: MarkAttendanceUiState = remember { demoMarkState },
    onBackClick: () -> Unit = {},
    onDateChange: (LocalDate) -> Unit = {},
    onMarkChange: (Boolean) -> Unit = {},
    viewModel: MarkAttendanceViewModel = viewModel( factory = AppViewModelProvider.Factory )
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Daily Attendance", maxLines = 1, overflow = TextOverflow.Ellipsis) },
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
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))

            /* ---------- Week day selector ---------- */
            WeekRow(selected = uiState.selectedDate, dates = uiState.weekDates, onSelect = onDateChange)

            Spacer(Modifier.height(24.dp))

            /* ---------- Big status circle ---------- */
            StatusCircle(isPresent = uiState.isPresent)

            Spacer(Modifier.height(24.dp))

            /* ---------- Toggle button ---------- */
            ToggleAttendanceButton(
                isPresent = uiState.isPresent,
                onToggle = onMarkChange
            )

            Spacer(Modifier.height(32.dp))

            /* ---------- Last 7‑day log ---------- */
            AttendanceHistoryList(log = uiState.history)
        }
    }
}

/* ---------------- COMPONENTS ---------------- */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeekRow(selected: LocalDate, dates: List<LocalDate>, onSelect: (LocalDate) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dates) { date ->
            val selectedColor by animateColorAsState(
                if (selected == date) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            )
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = selectedColor,
                modifier = Modifier
                    .size(width = 60.dp, height = 80.dp)
                    .clickable { onSelect(date) }
            ) {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusCircle(isPresent: Boolean) {
    val targetColor by animateColorAsState(
        if (isPresent) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
    )
    Box(
        Modifier
            .size(220.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(targetColor.copy(alpha = 0.9f), targetColor.copy(alpha = 0.6f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        val icon = if (isPresent) Icons.Default.Check else Icons.Default.Close
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(96.dp))
    }
}

@Composable
private fun ToggleAttendanceButton(isPresent: Boolean, onToggle: (Boolean) -> Unit) {
    ElevatedButton(
        onClick = { onToggle(!isPresent) },
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .animateContentSize()
    ) {
        val label = if (isPresent) "Mark Absent" else "Mark Present"
        Text(label, textAlign = TextAlign.Center)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AttendanceHistoryList(log: List<DayLog>) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Past Week", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        log.forEach { day ->
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
                    Text(day.date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()))
                    val statusColor by animateColorAsState(
                        if (day.present) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                    )
                    Text(
                        if (day.present) "Present" else "Absent",
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor
                    )
                }
            }
        }
    }
}

/* ---------------- MODELS ---------------- */

data class MarkAttendanceUiState(
    val selectedDate: LocalDate,
    val weekDates: List<LocalDate>,
    val isPresent: Boolean,
    val history: List<DayLog>
)

data class DayLog(val date: LocalDate, val present: Boolean)

/* ---------------- DEMO DATA ---------------- */

@RequiresApi(Build.VERSION_CODES.O)
private val today = LocalDate.now()
@RequiresApi(Build.VERSION_CODES.O)
private val demoWeek = List(7) { today.minusDays(3 - it.toLong()) }

private val demoMarkState: MarkAttendanceUiState
    @RequiresApi(Build.VERSION_CODES.O)
    get() = MarkAttendanceUiState(
        selectedDate = today,
        weekDates = demoWeek,
        isPresent = false,
        history = demoWeek.mapIndexed { i, date ->
            DayLog(date, present = i % 2 == 0)
        }
    )

/* ---------------- PREVIEW ---------------- */

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun MarkAttendancePreview() {
    MaterialTheme {
        MarkAttendanceScreen()
    }
}

package com.example.campussync.persentation.attendance.subject

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.campussync.domain.usecases.feature.subjects.Subject
import com.example.campussync.utils.config.TopBarConfig
import com.example.campussync.utils.views.AppLoading
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.Month


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectListScreen(
    onBackClick: () -> Unit,
    setTopBar: (TopBarConfig) -> Unit,
    viewModel: SubjectListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val subjects = uiState.subjects
    val isTeacher = uiState.isTeacher

    // Observe error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            //viewModel.clearErrorMessage()
        }
    }

    if(uiState.isLoading){
        AppLoading(
            isFullScreen = true
        )
    }

    LaunchedEffect(Unit) {
        TopBarConfig(
            title = "Subjects",
            showBackButton = true,
            onBackClick = {
                onBackClick.invoke()
            }
        )
    }

    SubjectListScreenContent(subjects, isTeacher)
}

@Composable
fun SubjectListScreenContent(
    subjects: List<Subject>,
    isTeacher: Boolean
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (subjects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SubjectCard(Subject(
                            id = 0,
                            name = "No Subjects Found",
                            code = "",
                            credits = 0
                        ), isTeacher = true)
                    }
                }
            } else {
                items(items = subjects, key = { it.id }) { subject ->
                    SubjectCard(subject, isTeacher)
                }
            }
        }
    }
}

@Composable
private fun SubjectCard(item: Subject, isTeacher: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row (
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Subject Title   >(icon)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.code,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                contentDescription = "Arrow in",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}


@Preview
@Composable
fun SubjectCardPreview() {
    SubjectListScreenContent(
        listOf(
            Subject(
                id = 1,
                name = "Mathematics",
                code = "CS-M01",
                credits = 34
            ),
            Subject(
                id = 2,
                name = "Computer Science",
                code = "CS-C01",
                credits = 45
            ),
            Subject(
                id = 3,
                name = "Physics",
                code = "CS-P01",
                credits = 3
            )
        ),
        isTeacher = true
    )
}


@Composable
private fun MonthFilterRow(
    selectedMonth: Int,
    onMonthSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val currentMonth = LocalDate.now().monthValue
        val monthNames = Month.entries.map { it.name }

        Text(
            text = "Filter by: ${Month.of(selectedMonth).name}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable {
                    val nextMonth = if (selectedMonth == 12) 1 else selectedMonth + 1
                    onMonthSelected(nextMonth)
                }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}


//@Composable
//private fun DetailedAttendanceCard(item: Subject, isTeacher: Boolean) {
//    val statusColor = when (item.status.lowercase()) {
//        "present" -> MaterialTheme.colorScheme.primary
//        "absent" -> MaterialTheme.colorScheme.error
//        "leave" -> MaterialTheme.colorScheme.secondary
//        else -> MaterialTheme.colorScheme.onSurfaceVariant
//    }
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Row(
//            modifier = Modifier.padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                Icon(
//                    imageVector = Icons.Default.DateRange,
//                    contentDescription = "Date",
//                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
//                    modifier = Modifier.size(24.dp)
//                )
//                Text(
//                    text = item.date?.format(DateTimeFormatter.ofPattern("MMM dd")) ?: "N/A",
//                    style = MaterialTheme.typography.bodyMedium,
//                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.onSurface
//                )
//                Text(
//                    text = item.date?.format(DateTimeFormatter.ofPattern("yyyy")) ?: "N/A",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//
//            Spacer(Modifier.width(16.dp))
//
//            Column(modifier = Modifier.weight(1f)) {
//                if (isTeacher) {
//                    Text(
//                        text = item.studentName ?: "Unknown Student",
//                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
//                        color = MaterialTheme.colorScheme.onSurface,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                    Text(
//                        text = "ID: ${item.studentId ?: "N/A"}",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                } else {
//                    Text(
//                        text = "Lecture on ${item.date?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: "N/A Date"}",
//                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
//                        color = MaterialTheme.colorScheme.onSurface,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                }
//            }
//
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(
//                    imageVector = when (item.status.lowercase()) {
//                        "present" -> Icons.Rounded.DoneAll
//                        "absent" -> Icons.Rounded.Close
//                        "leave" -> Icons.Default.Today
//                        else -> Icons.Rounded.CheckCircle
//                    },
//                    contentDescription = item.status,
//                    tint = statusColor,
//                    modifier = Modifier.size(24.dp)
//                )
//                Spacer(Modifier.width(8.dp))
//                Text(
//                    text = item.status,
//                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
//                    color = statusColor
//                )
//            }
//        }
//    }
//}

@Composable
fun AttendanceConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Confirm Attendance",
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                "Are you sure you want to mark yourself present for today?",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    )
}

@Composable
fun QrCodeDisplayDialog(
    qrCodeData: String,
    onDismiss: () -> Unit,
    isLoading: Boolean
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "QR Code for Attendance",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(100.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Generating QR...",
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    if (qrCodeData.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .size(250.dp)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                .clip(RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "QR Code Here: $qrCodeData",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        Text(
                            "Failed to generate QR Code.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text("Dismiss")
                }
            }
        }
    }
}
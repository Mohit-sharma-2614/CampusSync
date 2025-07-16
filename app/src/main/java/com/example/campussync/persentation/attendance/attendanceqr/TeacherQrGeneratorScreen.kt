package com.example.campussync.persentation.attendance.attendanceqr


import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.campussync.data.model.Subject
import com.example.campussync.utils.ConnectivityObserver
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.graphics.createBitmap

// Assuming your Subject data class is accessible

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherQrGeneratorScreen(
    viewModel: AttendanceQrViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val qrCodeData by viewModel.qrCodeData.collectAsState()
    val teacherMessage by viewModel.teacherMessage.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val isSubjectsLoading by viewModel.isSubjectsLoading.collectAsState()
    val subjectsErrorMessage by viewModel.subjectsErrorMessage.collectAsState()
    val absentMarkingStatus by viewModel.absentMarkingStatus.collectAsState() // Observe absent marking status
    val connectivityStatus by viewModel.connectivityStatus.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    var selectedSubject by remember { mutableStateOf<Subject?>(null) }

    // Update selectedSubject if the subjects list changes
    LaunchedEffect(subjects) {
        if (subjects.isNotEmpty()) {
            if (selectedSubject == null || !subjects.contains(selectedSubject)) {
                selectedSubject = subjects.firstOrNull()
            }
        } else {
            selectedSubject = null
        }
    }

    // Show snackbar for subjects error
    LaunchedEffect(subjectsErrorMessage) {
        subjectsErrorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Show snackbar for absent marking status
    LaunchedEffect(absentMarkingStatus) {
        when (val status = absentMarkingStatus) {
            is AttendanceQrViewModel.AttendanceMarkingStatus.Success -> {
                snackbarHostState.showSnackbar(status.message)
            }
            is AttendanceQrViewModel.AttendanceMarkingStatus.Error -> {
                snackbarHostState.showSnackbar(status.message)
            }
            else -> { /* Idle or Loading, do nothing */ }
        }
    }

    // Show snackbar for internet connectivity
    LaunchedEffect(connectivityStatus) {
        if (connectivityStatus == ConnectivityObserver.Status.Disconnected) {
            snackbarHostState.currentSnackbarData?.dismiss() // Dismiss any existing snackbar
            snackbarHostState.showSnackbar(
                message = "Internet is turned off. Please check your connection.",
                duration = SnackbarDuration.Indefinite // Keep visible until reconnected
            )
        } else {
            snackbarHostState.currentSnackbarData?.dismiss() // Dismiss when reconnected
        }
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Generate Attendance QR",
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            if (isSubjectsLoading) {
                CircularProgressIndicator()
                Text("Loading subjects...", modifier = Modifier.padding(top = 8.dp))
            } else if (subjects.isEmpty()) {
                Text(
                    text = subjectsErrorMessage ?: "No subjects assigned to you. Please contact administration.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                // Subject Selection
                Text(
                    "Select Subject:",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                subjects.forEach { subject ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedSubject = subject }
                    ) {
                        RadioButton(
                            selected = selectedSubject == subject,
                            onClick = { selectedSubject = subject }
                        )
                        Text(text = subject.name)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        selectedSubject?.let {
                            viewModel.generateAttendanceTokenForQr(it)
                        } ?: run {
                            coroutineScope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar("Please select a subject first.")
                            }
                        }
                    },
                    enabled = selectedSubject != null
                ) {
                    Text("Generate QR Code")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mark Absent Students Button
                Button(
                    onClick = {
                        selectedSubject?.let {
                            viewModel.markAbsentStudents(it)
                        } ?: run {
                            coroutineScope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar("Please select a subject first.")
                            }
                        }
                    },
                    enabled = selectedSubject != null && qrCodeData != null, // Enable only if subject selected and QR code generated
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Mark Absent Students")
                }

                Spacer(modifier = Modifier.height(24.dp))

                qrCodeData?.let { data ->
                    val bitmap = remember(data) {
                        try {
                            val writer = QRCodeWriter()
                            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
                            val width = bitMatrix.width
                            val height = bitMatrix.height
                            val pixels = IntArray(width * height)
                            for (y in 0 until height) {
                                val offset = y * width
                                for (x in 0 until width) {
                                    pixels[offset + x] = if (bitMatrix.get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
                                }
                            }
                            createBitmap(width, height, Bitmap.Config.RGB_565).apply {
                                setPixels(pixels, 0, width, 0, 0, width, height)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }

                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Attendance QR Code",
                            modifier = Modifier.size(200.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    } ?: run {
                        Text("Error generating QR code image.", color = MaterialTheme.colorScheme.error)
                    }
                }

                teacherMessage?.let { message ->
                    Text(
                        text = message,
                        color = if (message.contains("Error")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    LaunchedEffect(message) {
                        if (message.isNotBlank()) {
                            delay(3000)
                            viewModel.resetTeacherMessage()
                        }
                    }
                }

                // Display absent marking status
                when (val status = absentMarkingStatus) {
                    is AttendanceQrViewModel.AttendanceMarkingStatus.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                        Text("Marking absent students...", modifier = Modifier.padding(top = 8.dp))
                    }
                    is AttendanceQrViewModel.AttendanceMarkingStatus.Success -> {
                        Text(
                            text = status.message,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        LaunchedEffect(status) {
                            delay(3000)
                            viewModel.resetAbsentMarkingStatus() // Add this function to ViewModel
                        }
                    }
                    is AttendanceQrViewModel.AttendanceMarkingStatus.Error -> {
                        Text(
                            text = status.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        LaunchedEffect(status) {
                            delay(3000)
                            viewModel.resetAbsentMarkingStatus() // Add this function to ViewModel
                        }
                    }
                    is AttendanceQrViewModel.AttendanceMarkingStatus.Idle -> {
                        // No UI for idle state
                    }
                }
            }
        }
    }
}
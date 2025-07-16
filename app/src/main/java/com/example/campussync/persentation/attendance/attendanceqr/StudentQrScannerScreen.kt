package com.example.campussync.persentation.attendance.attendanceqr

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.campussync.data.model.attendanceToken.AttendanceToken
import java.time.Instant // For converting timestamps from QR token
import java.time.OffsetDateTime
import java.time.ZoneId // For converting timestamps from QR token
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors

// Assuming these are your data classes:
// data class StudentLoginResponse(val id: Long, val name: String, val studentUid: String, val email: String, val token: String, val semester: Int)
// data class Subject(val id: Long, val name: String, val code: String, val semester: Int, val department: Department, val teacher: TeacherLoginResponse)
// data class AttendanceToken(val subject: Subject, val generatedAt: Long, val expiresAt: Long) // From the QR code


@OptIn(ExperimentalGetImage::class)
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentQrScannerScreen(
    viewModel: AttendanceQrViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val scannedQrContent by viewModel.scannedQrContent.collectAsState()
    val attendanceMarkingStatus by viewModel.attendanceMarkingStatus.collectAsState()
    val studentLoginResponse by viewModel.studentLoginResponse.collectAsState() // Observe student info from ViewModel

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            // Handle cases where permission is denied, maybe show a persistent message or navigate back
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Scan QR for Attendance",
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (hasCameraPermission) {
                if (scannedQrContent == null) {
                    // Camera preview for scanning
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()

                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }

                                val imageAnalysis = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                    .also { analysis ->
                                        analysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                                            val mediaImage = imageProxy.image
                                            if (mediaImage != null) {
                                                val inputImage = InputImage.fromMediaImage(
                                                    mediaImage,
                                                    imageProxy.imageInfo.rotationDegrees
                                                )
                                                val options = BarcodeScannerOptions.Builder()
                                                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                                                    .build()
                                                val scanner = BarcodeScanning.getClient(options)

                                                scanner.process(inputImage)
                                                    .addOnSuccessListener { barcodes ->
                                                        for (barcode in barcodes) {
                                                            barcode.rawValue?.let { content ->
                                                                viewModel.onQrCodeScanned(content)
                                                                // Stop analysis after first successful scan
                                                                // Important: Clear analyzer *after* processing,
                                                                // and ensure imageProxy.close() is called
                                                                // It's often safer to reset the analyzer on state change
                                                                // or navigate away. For now, we rely on content != null check
                                                            }
                                                        }
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.e("QRScanner", "Barcode scanning failed", e)
                                                    }
                                                    .addOnCompleteListener {
                                                        // Always close the image proxy
                                                        imageProxy.close()
                                                    }
                                            } else {
                                                imageProxy.close() // Close even if mediaImage is null
                                            }
                                        }
                                    }

                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        imageAnalysis
                                    )
                                } catch (exc: Exception) {
                                    Log.e("QRScanner", "Use case binding failed", exc)
                                }
                            }, ContextCompat.getMainExecutor(ctx))
                            previewView
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text("Point your camera at the QR code...", style = MaterialTheme.typography.titleMedium)
                    if (studentLoginResponse == null) {
                        CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                        Text("Fetching student info...", modifier = Modifier.padding(top = 8.dp))
                    }
                } else {
                    // Display scanned content and confirmation
                    val attendanceToken: AttendanceToken? = remember(scannedQrContent) {
                        try {
                            Gson().fromJson(scannedQrContent, AttendanceToken::class.java)
                        } catch (e: JsonSyntaxException) {
                            null
                        }
                    }

                    if (attendanceToken != null) {
                        val zoneId = ZoneId.of("Asia/Kolkata") // India's timezone
                        val outputFormatter = DateTimeFormatter.ofPattern("hh:mm a, dd MMM yyyy").withZone(zoneId)

                        val generatedAtInstant: Instant
                        if (attendanceToken.generatedAt is String) { // Check if it's a String
                            // Parse the ISO 8601 string directly
                            generatedAtInstant = OffsetDateTime.parse(attendanceToken.generatedAt as String).toInstant()
                        } else {
                            // Handle unexpected type or log an error
                            Log.e("QRScanner", "Unexpected type for generatedAt: ${attendanceToken.generatedAt?.javaClass}")
                            // Potentially set a default or show an error message
                            return@Scaffold // Or handle appropriately
                        }
                        val generatedAtDateTime = outputFormatter.format(generatedAtInstant)


                        // Assuming attendanceToken.expiresAt is a String like "2025-07-14T11:40:18.988+00:00"
                        val expiresAtInstant: Instant
                        if (attendanceToken.expiresAt is String) {
                            expiresAtInstant = OffsetDateTime.parse(attendanceToken.expiresAt as String).toInstant()
                        } else {
                            Log.e("QRScanner", "Unexpected type for expiresAt: ${attendanceToken.expiresAt?.javaClass}")
                            return@Scaffold // Or handle appropriately
                        }
                        val expiresAtDateTime = outputFormatter.format(expiresAtInstant)

                        Text("Scanned QR Code Details:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Subject: ${attendanceToken.subject.name}")
                        Text("Generated At: $generatedAtDateTime")
                        Text("Expires At: $expiresAtDateTime")
                        Spacer(modifier = Modifier.height(16.dp))

                        if (studentLoginResponse == null) {
                            CircularProgressIndicator()
                            Text("Fetching student details to mark attendance...")
                        } else {
                            Button(
                                onClick = {
                                    // Pass the attendance token; ViewModel already has studentLoginResponse
                                    viewModel.markAttendance(attendanceToken)
                                },
                                enabled = attendanceMarkingStatus !is AttendanceQrViewModel.AttendanceMarkingStatus.Loading
                            ) {
                                Text("Confirm and Mark Attendance")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.resetScannedContentAndStatus() }
                        ) {
                            Text("Scan Another QR Code")
                        }

                        when (attendanceMarkingStatus) {
                            is AttendanceQrViewModel.AttendanceMarkingStatus.Loading -> {
                                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                                Text("Marking attendance...", modifier = Modifier.padding(top = 8.dp))
                            }
                            is AttendanceQrViewModel.AttendanceMarkingStatus.Success -> {
                                Text((attendanceMarkingStatus as AttendanceQrViewModel.AttendanceMarkingStatus.Success).message, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 16.dp))
                            }
                            is AttendanceQrViewModel.AttendanceMarkingStatus.Error -> {
                                val errorMessage = (attendanceMarkingStatus as AttendanceQrViewModel.AttendanceMarkingStatus.Error).message
                                Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
                            }
                            else -> {} // Idle state
                        }
                    } else {
                        Text("Invalid QR Code content scanned. Please scan a valid attendance QR.", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.resetScannedContentAndStatus() }
                        ) {
                            Text("Scan Again")
                        }
                    }
                }
            } else {
                Text("Camera permission is required to scan QR codes.", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { requestPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant Camera Permission")
                }
            }
        }
    }
}
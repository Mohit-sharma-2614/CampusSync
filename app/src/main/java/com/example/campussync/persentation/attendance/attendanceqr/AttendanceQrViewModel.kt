package com.example.campussync.persentation.attendance.attendanceqr


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.isEmpty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campussync.api.AttendanceApiService
import com.example.campussync.api.AttendanceTokenApiService
import com.example.campussync.data.model.attendance.Attendance
import com.example.campussync.data.model.attendanceToken.AttendanceToken
import com.example.campussync.data.model.Subject
import com.example.campussync.data.model.attendance.AttendanceReq
import com.example.campussync.data.model.attendanceToken.AttendanceTokenReq
import com.example.campussync.data.model.student.StudentLoginResponse
import com.example.campussync.data.repository.AttendanceRepository
import com.example.campussync.data.repository.EnrollmentRepository
import com.example.campussync.data.repository.StudentRepository
import com.example.campussync.data.repository.SubjectRepository // Import SubjectRepository
import com.example.campussync.utils.ConnectivityObserver
import com.example.campussync.utils.Resource
import com.example.campussync.utils.UserPreferences
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AttendanceQrViewModel @Inject constructor(
    private val attendanceTokenApiService: AttendanceTokenApiService,
    private val attendanceApiService: AttendanceApiService,
    private val studentRepository: StudentRepository,
    private val subjectRepository: SubjectRepository,
    private val attendanceRepository: AttendanceRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val userPreferences: UserPreferences,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    val connectivityStatus: StateFlow<ConnectivityObserver.Status> = connectivityObserver.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConnectivityObserver.Status.Disconnected)

    // Teacher QR Code Generation State
    private val _qrCodeData = MutableStateFlow<String?>(null)
    val qrCodeData: StateFlow<String?> = _qrCodeData.asStateFlow()

    // New state for marking absent students
    private val _absentMarkingStatus = MutableStateFlow<AttendanceMarkingStatus>(AttendanceMarkingStatus.Idle)
    val absentMarkingStatus: StateFlow<AttendanceMarkingStatus> = _absentMarkingStatus.asStateFlow()

    private val _teacherMessage = MutableStateFlow<String?>(null)
    val teacherMessage: StateFlow<String?> = _teacherMessage.asStateFlow()

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _isSubjectsLoading = MutableStateFlow<Boolean>(false)
    val isSubjectsLoading: StateFlow<Boolean> = _isSubjectsLoading.asStateFlow()

    private val _subjectsErrorMessage = MutableStateFlow<String?>(null)
    val subjectsErrorMessage: StateFlow<String?> = _subjectsErrorMessage.asStateFlow()

    // Student QR Code Scanning State
    private val _scannedQrContent = MutableStateFlow<String?>(null)
    val scannedQrContent: StateFlow<String?> = _scannedQrContent.asStateFlow()

    private val _attendanceMarkingStatus = MutableStateFlow<AttendanceMarkingStatus>(AttendanceMarkingStatus.Idle) // This seems to be a duplicate of _absentMarkingStatus. Consider removing if it serves the same purpose.
    val attendanceMarkingStatus: StateFlow<AttendanceMarkingStatus> = _attendanceMarkingStatus.asStateFlow()

    // User-specific data fetched from UserPreferences
    private val _currentUserId = MutableStateFlow<Long>(0L)
    val currentUserId: StateFlow<Long> = _currentUserId.asStateFlow()

    private val _isCurrentUserTeacher = MutableStateFlow<Boolean>(false)
    val isCurrentUserTeacher: StateFlow<Boolean> = _isCurrentUserTeacher.asStateFlow()

    // Student's full login response (needed for Attendance object)
    private val _studentLoginResponse = MutableStateFlow<StudentLoginResponse?>(null)
    val studentLoginResponse: StateFlow<StudentLoginResponse?> = _studentLoginResponse.asStateFlow()

    sealed class AttendanceMarkingStatus {
        data object Idle : AttendanceMarkingStatus()
        data object Loading : AttendanceMarkingStatus()
        data class Success(val message: String) : AttendanceMarkingStatus()
        data class Error(val message: String) : AttendanceMarkingStatus()
    }

    init {
        viewModelScope.launch {
            combine(userPreferences.userId, userPreferences.isTeacher) { userIdStr, isTeacher ->
                val id = userIdStr?.toLongOrNull() ?: 0L
                id to isTeacher
            }.collectLatest { (userId, isTeacher) ->
                _currentUserId.value = userId
                _isCurrentUserTeacher.value = isTeacher

                Log.d("AttendanceQrVM", "UserPrefs: ID=$userId, Teacher=$isTeacher")

                if (!isTeacher && userId != 0L) {
                    fetchStudentDetails(userId)
                    _subjects.value = emptyList()
                    _isSubjectsLoading.value = false
                    _subjectsErrorMessage.value = null
                } else if (isTeacher && userId != 0L) {
                    _studentLoginResponse.value = null
                    fetchSubjectsForTeacher(userId)
                } else {
                    _studentLoginResponse.value = null
                    _subjects.value = emptyList()
                    _isSubjectsLoading.value = false
                    _subjectsErrorMessage.value = null
                }
            }
        }
    }

    /**
     * Teacher Function: Marks all non-present students as absent for a subject and date.
     * Ensures students marked as PRESENT via QR code are not marked absent.
     * Prevents re-marking of already absent students.
     * Uses Flow to handle attendance data reactively.
     * @param subject The subject for which to mark absent students.
     * @param date The date of the session (defaults to today).
     */
    fun markAbsentStudents(subject: Subject, date: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)) {
        viewModelScope.launch {
            if (!_isCurrentUserTeacher.value) {
                _absentMarkingStatus.value = AttendanceMarkingStatus.Error("Only teachers can mark absent students.")
                Log.e("AttendanceQrVM", "markAbsentStudents: User is not a teacher.")
                return@launch
            }

            _absentMarkingStatus.value = AttendanceMarkingStatus.Loading
            try {
                // Step 1: Fetch all enrolled students for the subject
                // Assuming getEnrollmentsBySubjectId is a suspend function returning Resource directly
                // or a Flow that you handle appropriately (e.g., with .firstOrNull() if it's a one-shot)
                val enrolledStudentsResult = enrollmentRepository.getEnrollmentsBySubjectId(subject.id) // Adjust if this is a Flow
                if (enrolledStudentsResult !is Resource.Success) {
                    val errorMessage = if (enrolledStudentsResult is Resource.Error) {
                        enrolledStudentsResult.message ?: "Unknown error fetching enrollments"
                    } else {
                        "Enrollments are still loading or an unexpected state occurred."
                    }
                    _absentMarkingStatus.value = AttendanceMarkingStatus.Error(
                        "Failed to fetch enrollments: $errorMessage"
                    )
                    Log.e("AttendanceQrVM", "Error fetching enrollments: $errorMessage")
                    return@launch
                }
                val enrolledStudents = enrolledStudentsResult.data.map { it.student.id }.toSet()
                Log.d("AttendanceQrVM", "Enrolled students for subject ${subject.id}: ${enrolledStudents.size}")


                // Step 2: Fetch current attendance records.
                // We will collect the flow until we get a non-Loading state.
                var attendanceRecordsResult: Resource<List<Attendance>>? = null
                attendanceRepository.getAttendanceBySubjectAndDate(subject.id, date)
                    .collectLatest { result -> // Use collectLatest or collect
                        if (result !is Resource.Loading) {
                            attendanceRecordsResult = result
                            // Once we have a definitive result (Success or Error), we can stop collecting
                            // if this Flow is only meant to provide one such result.
                            // For simplicity here, we assume the flow will eventually complete or
                            // this collectLatest will handle new emissions appropriately if it's a long-lived flow.
                            // If it's a one-shot flow (Loading -> Success/Error), this is fine.
                        }
                        // If it's loading, we just wait. _absentMarkingStatus is already Loading.

                        // If we have a result, process it.
                        attendanceRecordsResult?.let { finalResult ->
                            if (finalResult is Resource.Success) {
                                val attendanceRecords = finalResult.data
                                Log.d("AttendanceQrVM", "Attendance records for subject ${subject.id} on $date: ${attendanceRecords.size ?: 0}")
                                processAndMarkAbsentees(enrolledStudents, attendanceRecords ?: emptyList(), subject, date)
                            } else if (finalResult is Resource.Error) {
                                _absentMarkingStatus.value = AttendanceMarkingStatus.Error(
                                    "Failed to fetch attendance records: ${finalResult.message ?: "Unknown error"}"
                                )
                                Log.e("AttendanceQrVM", "Error fetching attendance: ${finalResult.message}")
                            }
                            // To prevent re-processing if the flow emits further,
                            // you might need to cancel the job or use a different collection strategy
                            // like .first { it !is Resource.Loading } if applicable and safe.
                            // For this example, we'll assume collectLatest handles it well enough
                            // or the flow naturally completes after Success/Error.
                            // To be absolutely sure for a one-shot operation after loading:
                            if (finalResult !is Resource.Loading) {
                                // Effectively stop further collection logic for this specific call.
                                // The actual cancellation of the Flow collection would be managed by
                                // viewModelScope or if you explicitly cancel the launch Job.
                                // For now, we just ensure processAndMarkAbsentees or error handling
                                // happens once per definitive result.
                                attendanceRecordsResult = null // Reset to prevent re-entry if flow has more emissions
                                coroutineContext.cancel() // Stop this specific collect
                            }
                        }
                    }
                // The logic that depended on attendanceRecordsResult is now inside the collect block's handling
                // or in the extracted function processAndMarkAbsentees.

            } catch (e: Exception) {
                // This catch block will now mostly catch issues from getEnrollmentsBySubjectId if it throws
                // or other unexpected exceptions outside the flow collection.
                // Flow collection exceptions are handled by the .catch operator in the repository
                // and should result in Resource.Error being emitted.
                _absentMarkingStatus.value = AttendanceMarkingStatus.Error("Error marking absent students: ${e.message}")
                Log.e("AttendanceQrVM", "Exception during absent student marking process", e)
            }
        }
    }

    // Extracted logic from markAbsentStudents to handle data after fetching attendance records
    private fun processAndMarkAbsentees(
        enrolledStudents: Set<Long>,
        attendanceRecords: List<Attendance>,
        subject: Subject,
        date: String
    ) {
        viewModelScope.launch { // Keep this on viewModelScope for consistency
            val presentStudentIds = attendanceRecords
                .filter { it.status.equals("PRESENT", ignoreCase = true) }
                .map { it.student.id }
                .toSet()
            val absentStudentIdsAlreadyMarked = attendanceRecords
                .filter { it.status.equals( "ABSENT", ignoreCase = true )}
                .map { it.student.id }
                .toSet()
            Log.d("AttendanceQrVM", "Present students: ${presentStudentIds.size}, Already absent: ${absentStudentIdsAlreadyMarked.size}")

            val studentsToMarkAbsent = enrolledStudents - presentStudentIds - absentStudentIdsAlreadyMarked

            if (studentsToMarkAbsent.isEmpty()) {
                _absentMarkingStatus.value = AttendanceMarkingStatus.Success(
                    if (absentStudentIdsAlreadyMarked.isNotEmpty() || presentStudentIds.isNotEmpty()) {
                        "All enrolled students are already marked as present or absent for ${subject.name} on $date."
                    } else {
                        "No students to mark absent for ${subject.name} on $date."
                    }
                )
                Log.d("AttendanceQrVM", "No students left to mark absent.")
                return@launch
            }

            val absentAttendanceRecords = studentsToMarkAbsent.map { studentId ->
                AttendanceReq(
                    studentId = studentId,
                    subjectId = subject.id,
                    status = "ABSENT"
                )
            }

            when (val createResult = attendanceRepository.createBulkAttendance(absentAttendanceRecords)) {
                is Resource.Success -> {
                    _absentMarkingStatus.value = AttendanceMarkingStatus.Success(
                        "${absentAttendanceRecords.size} students marked absent for ${subject.name} on $date."
                    )
                    Log.d("AttendanceQrVM", "Successfully marked ${absentAttendanceRecords.size} students as absent.")
                }
                is Resource.Error -> {
                    _absentMarkingStatus.value = AttendanceMarkingStatus.Error(
                        "Failed to mark absent students: ${createResult.message}"
                    )
                    Log.e("AttendanceQrVM", "Error marking absent students: ${createResult.message}")
                }
                is Resource.Loading -> { /* Should ideally not happen here if createBulkAttendance is suspend */
                }
            }
        }
    }


    private fun fetchStudentDetails(studentId: Long) {
        viewModelScope.launch {
            Log.d("AttendanceQrVM", "Attempting to fetch student details for ID: $studentId")
            // Assuming getStudentById is a suspend fun or a Flow handled with .first() or .collect()
            // For simplicity, if it's a suspend fun returning Resource:
            when (val result = studentRepository.getStudentById(studentId)) {
                is Resource.Success -> {
                    val student = result.data
                    val mappedStudent = StudentLoginResponse(
                        id = student.id,
                        name = student.name,
                        studentUid = student.studentUid,
                        email = student.email,
                        token = "", // Token might not be needed here or fetched differently
                        semester = student.semester
                    )
                    _studentLoginResponse.value = mappedStudent
                    Log.d("AttendanceQrVM", "Student details fetched successfully: ${mappedStudent.name}")
                }
                is Resource.Error -> {
                    Log.e("AttendanceQrVM", "Failed to fetch student details: ${result.message}")
                    _studentLoginResponse.value = null
                }
                is Resource.Loading -> { /* ViewModel handles overall loading state if necessary */
                }
            }
        }
    }

    private fun fetchSubjectsForTeacher(teacherId: Long) {
        viewModelScope.launch {
            _isSubjectsLoading.value = true
            _subjectsErrorMessage.value = null
            Log.d("AttendanceQrVM", "Attempting to fetch subjects for teacher ID: $teacherId")
            // Assuming getAllSubjects is a suspend fun or a Flow handled appropriately
            when (val result = subjectRepository.getAllSubjects()) { // Adjust if it's a Flow
                is Resource.Success -> {
                    val teacherSubjects = result.data.filter { it.teacher?.id == teacherId }
                    _subjects.value = teacherSubjects
                    _isSubjectsLoading.value = false
                    if (teacherSubjects.isEmpty()) {
                        _subjectsErrorMessage.value = "No subjects assigned to you."
                    }
                    Log.d("AttendanceQrVM", "Subjects fetched: ${teacherSubjects.size} subjects.")
                }
                is Resource.Error -> {
                    val errorMessage = result.message ?: "Failed to load subjects."
                    _subjectsErrorMessage.value = errorMessage
                    _isSubjectsLoading.value = false
                    _subjects.value = emptyList()
                    Log.e("AttendanceQrVM", "Error fetching subjects: $errorMessage")
                }
                is Resource.Loading -> { /* _isSubjectsLoading handles this */
                }
            }
        }
    }

    /**
     * Teacher Function: Generates an AttendanceToken and updates QR code data.
     * @param subject The subject for which the attendance token is being generated.
     */
    fun generateAttendanceTokenForQr(subject: Subject) {
        viewModelScope.launch {
            _teacherMessage.value = null
            _qrCodeData.value = null // Reset QR code data
            if (!_isCurrentUserTeacher.value) {
                _teacherMessage.value = "Error: Only teachers can generate QR codes."
                return@launch
            }
            if (_currentUserId.value == 0L) { // Ensure teacher ID is available
                _teacherMessage.value = "Error: Teacher ID not available."
                return@launch
            }

            try {
                // Construct the request body for creating an attendance token
                val newTokenReq = AttendanceTokenReq(
                    subjectId = subject.id
                    // Add teacherId if your backend requires it and it's not inferred
                )
                Log.d("AttendanceQrVM", "Generating token for subject ID: ${subject.id}")

                // Call the API service to create the token
                val response = attendanceTokenApiService.createAttendanceToken(newTokenReq)

                if (response.isSuccessful && response.body() != null) {
                    val createdToken = response.body()!! // Non-null asserted because of the check
                    // Serialize the token to JSON for the QR code
                    _qrCodeData.value = Gson().toJson(createdToken)
                    _teacherMessage.value = "QR Code generated successfully for ${createdToken.subject.name}!"
                    Log.d("AttendanceQrVM", "Generated QR Code Data: ${_qrCodeData.value}")
                } else {
                    // Handle API error response
                    val errorMsg = response.errorBody()?.string() ?: "Failed to generate token (Unknown API error)"
                    _teacherMessage.value = "Failed to generate attendance token: $errorMsg (Code: ${response.code()})"
                    Log.e("AttendanceQrVM", "API error generating token: $errorMsg, Code: ${response.code()}")
                }
            } catch (e: Exception) {
                // Handle exceptions like network errors
                _teacherMessage.value = "Error generating attendance token: ${e.message}"
                Log.e("AttendanceQrVM", "Exception generating token", e)
            }
        }
    }


    /**
     * Student Function: Processes scanned QR code content.
     * @param content The string content extracted from the QR code.
     */
    fun onQrCodeScanned(content: String) {
        _scannedQrContent.value = content
        _attendanceMarkingStatus.value = AttendanceMarkingStatus.Idle // Reset status for new scan
        Log.d("AttendanceQrVM", "QR Code scanned: $content")
    }

    /**
     * Student Function: Confirms and marks attendance after scanning.
     * Includes a check to prevent multiple attendance markings for the same subject on the same day.
     * @param attendanceToken The AttendanceToken parsed from the QR code.
     */
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7) // If any internal calls need it
    fun markAttendance(attendanceToken: AttendanceToken) {
        viewModelScope.launch {
            _attendanceMarkingStatus.value = AttendanceMarkingStatus.Loading

            val student = _studentLoginResponse.value
            if (student == null) {
                _attendanceMarkingStatus.value = AttendanceMarkingStatus.Error("Student information not available. Please try logging in again.")
                Log.e("AttendanceQrVM", "markAttendance: StudentLoginResponse is null.")
                return@launch
            }

            val todayDateString = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

            // Check for existing attendance for the same subject and student today
            // Assuming getAttendanceBySubjectAndStudentId returns Flow<Resource<List<Attendance>>>
            // We need to collect the actual result, not just the loading state.
            // Using .first { it !is Resource.Loading } is a concise way if the flow guarantees a non-loading state eventually.
            try {
                val existingAttendanceResource = attendanceRepository
                    .getAttendanceBySubjectAndStudentId(
                        attendanceToken.subject.id,
                        student.id
                    )

                when (existingAttendanceResource) {
                    is Resource.Success -> {
                        val existingRecordsToday = existingAttendanceResource.data.filter {
                            it.date == todayDateString
                        }
                        if (existingRecordsToday.isNotEmpty()) {
                            _attendanceMarkingStatus.value = AttendanceMarkingStatus.Error(
                                "You have already marked attendance for ${attendanceToken.subject.name} today."
                            )
                            Log.w("AttendanceQrVM", "Student ${student.id} already has attendance for subject ${attendanceToken.subject.name} on $todayDateString.")
                            return@launch
                        }
                        // Continue to mark attendance
                    }
                    is Resource.Error -> {
                        Log.e("AttendanceQrVM", "Error checking for existing attendance: ${existingAttendanceResource.message}")
                        _attendanceMarkingStatus.value = AttendanceMarkingStatus.Error(
                            "Failed to verify existing attendance. Please try again or contact support. Error: ${existingAttendanceResource.message}"
                        )
                        return@launch
                    }
                    else -> {
                        Log.e("AttendanceQrVM", "Unexpected state in flow: $existingAttendanceResource")
                        _attendanceMarkingStatus.value = AttendanceMarkingStatus.Error("Unexpected error occurred. Try again.")
                        return@launch
                    }
                }
            } catch (e: Exception) {
                Log.e("AttendanceQrVM", "Exception checking existing attendance: ${e.message}", e)
                _attendanceMarkingStatus.value = AttendanceMarkingStatus.Error(
                    "An error occurred while checking existing attendance: ${e.message}"
                )
                return@launch
            }



            // Proceed to mark attendance
            val attendanceReq = AttendanceReq(
                studentId = student.id,
                subjectId = attendanceToken.subject.id,
                status = "Present".uppercase(Locale.getDefault()) // Assuming "Present" is the correct status string
                // date is typically set by the backend based on creation time
            )
            Log.d("AttendanceQrVM", "Attempting to mark attendance for student ${student.id} in subject ${attendanceToken.subject.name}")

            try {
                val response = attendanceApiService.createAttendance(attendanceReq)
                if (response.isSuccessful && response.body() != null) {
                    _attendanceMarkingStatus.value = AttendanceMarkingStatus.Success("Attendance marked successfully for ${attendanceToken.subject.name}!")
                    Log.d("AttendanceQrVM", "Attendance marked successfully. Response: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = if (response.code() == 409 && errorBody?.contains("already marked", ignoreCase = true) == true) {
                        // More specific message for conflict if backend indicates already marked
                        "You have already marked attendance for this session today."
                    } else {
                        errorBody ?: "Unknown error from server"
                    }
                    _attendanceMarkingStatus.value = AttendanceMarkingStatus.Error("Failed to mark attendance: $errorMsg (Code: ${response.code()})")
                    Log.e("AttendanceQrVM", "API error marking attendance: $errorMsg, Code: ${response.code()}")
                }
            } catch (e: Exception) {
                _attendanceMarkingStatus.value = AttendanceMarkingStatus.Error("Error marking attendance: ${e.message}")
                Log.e("AttendanceQrVM", "Exception marking attendance", e)
            }
        }
    }


    fun resetAbsentMarkingStatus() {
        _absentMarkingStatus.value = AttendanceMarkingStatus.Idle
        Log.d("AttendanceQrVM", "Resetting absent marking status.")
    }

    fun resetTeacherMessage() {
        _teacherMessage.value = null
    }

    fun resetScannedContentAndStatus() {
        _scannedQrContent.value = null
        // _attendanceMarkingStatus is likely tied to student marking, _absentMarkingStatus to teacher.
        // Decide which one to reset or if both need resetting based on context.
        // For a general reset from student scanning screen:
        _attendanceMarkingStatus.value = AttendanceMarkingStatus.Idle
        Log.d("AttendanceQrVM", "Resetting scanned content and student attendance status.")
    }
}

//@HiltViewModel
//class AttendanceQrViewModel @Inject constructor(
//    private val attendanceTokenApiService: AttendanceTokenApiService,
//    private val attendanceApiService: AttendanceApiService,
//    private val studentRepository: StudentRepository,
//    private val subjectRepository: SubjectRepository, // Inject SubjectRepository
//    private val userPreferences: UserPreferences
//) : ViewModel() {
//
//    // Teacher QR Code Generation State
//    private val _qrCodeData = MutableStateFlow<String?>(null)
//    val qrCodeData: StateFlow<String?> = _qrCodeData.asStateFlow()
//
//    private val _teacherMessage = MutableStateFlow<String?>(null)
//    val teacherMessage: StateFlow<String?> = _teacherMessage.asStateFlow()
//
//    // NEW: Subject list for teacher
//    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
//    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()
//
//    private val _isSubjectsLoading = MutableStateFlow<Boolean>(false)
//    val isSubjectsLoading: StateFlow<Boolean> = _isSubjectsLoading.asStateFlow()
//
//    private val _subjectsErrorMessage = MutableStateFlow<String?>(null)
//    val subjectsErrorMessage: StateFlow<String?> = _subjectsErrorMessage.asStateFlow()
//
//
//    // Student QR Code Scanning State
//    private val _scannedQrContent = MutableStateFlow<String?>(null)
//    val scannedQrContent: StateFlow<String?> = _scannedQrContent.asStateFlow()
//
//    private val _attendanceMarkingStatus = MutableStateFlow<AttendanceMarkingStatus>(AttendanceMarkingStatus.Idle)
//    val attendanceMarkingStatus: StateFlow<AttendanceMarkingStatus> = _attendanceMarkingStatus.asStateFlow()
//
//    // User-specific data fetched from UserPreferences
//    private val _currentUserId = MutableStateFlow<Long>(0L)
//    val currentUserId: StateFlow<Long> = _currentUserId.asStateFlow()
//
//    private val _isCurrentUserTeacher = MutableStateFlow<Boolean>(false)
//    val isCurrentUserTeacher: StateFlow<Boolean> = _isCurrentUserTeacher.asStateFlow()
//
//    // Student's full login response (needed for Attendance object)
//    private val _studentLoginResponse = MutableStateFlow<StudentLoginResponse?>(null)
//    val studentLoginResponse: StateFlow<StudentLoginResponse?> = _studentLoginResponse.asStateFlow()
//
//
//    sealed class AttendanceMarkingStatus {
//        data object Idle : AttendanceMarkingStatus()
//        data object Loading : AttendanceMarkingStatus()
//        data class Success(val message: String) : AttendanceMarkingStatus()
//        data class Error(val message: String) : AttendanceMarkingStatus()
//    }
//
//    init {
//        // Collect user preferences to update internal state and trigger data fetches
//        viewModelScope.launch {
//            combine(userPreferences.userId, userPreferences.isTeacher) { userIdStr, isTeacher ->
//                val id = userIdStr?.toLongOrNull() ?: 0L
//                id to isTeacher
//            }.collectLatest { (userId, isTeacher) ->
//                _currentUserId.value = userId
//                _isCurrentUserTeacher.value = isTeacher
//
//                Log.d("AttendanceQrVM", "UserPrefs: ID=$userId, Teacher=$isTeacher")
//
//                // If it's a student, fetch their full details
//                if (!isTeacher && userId != 0L) {
//                    fetchStudentDetails(userId)
//                    // Ensure subjects are cleared if user is a student
//                    _subjects.value = emptyList()
//                    _isSubjectsLoading.value = false
//                    _subjectsErrorMessage.value = null
//                } else if (isTeacher && userId != 0L) {
//                    // If it's a teacher, fetch subjects
//                    _studentLoginResponse.value = null // Clear student details
//                    fetchSubjectsForTeacher(userId) // Pass teacher ID to filter subjects
//                } else {
//                    // Reset all user-specific data if ID is 0 or invalid state
//                    _studentLoginResponse.value = null
//                    _subjects.value = emptyList()
//                    _isSubjectsLoading.value = false
//                    _subjectsErrorMessage.value = null
//                }
//            }
//        }
//    }
//
//    /**
//     * Fetches the full StudentLoginResponse for the current user.
//     * This is crucial for constructing the Attendance object for marking attendance.
//     */
//    private fun fetchStudentDetails(studentId: Long) {
//        viewModelScope.launch {
//            Log.d("AttendanceQrVM", "Attempting to fetch student details for ID: $studentId")
//            when (val result = studentRepository.getStudentById(studentId)) {
//                is Resource.Success -> {
//                    val student = result.data
//                    val mappedStudent = StudentLoginResponse(
//                        id = student.id,
//                        name = student.name,
//                        studentUid = student.studentUid,
//                        email = student.email,
//                        token = "",
//                        semester = student.semester
//                    )
//                    _studentLoginResponse.value = mappedStudent
//                    Log.d("AttendanceQrVM", "Student details fetched successfully: ${mappedStudent.name}")
//                }
//                is Resource.Error -> {
//                    Log.e("AttendanceQrVM", "Failed to fetch student details: ${result.message}")
//                    _studentLoginResponse.value = null
//                }
//                is Resource.Loading -> { /* Do nothing here */ }
//            }
//        }
//    }
//
//    /**
//     * NEW FUNCTION: Fetches subjects assigned to the current teacher.
//     */
//    private fun fetchSubjectsForTeacher(teacherId: Long) {
//        viewModelScope.launch {
//            _isSubjectsLoading.value = true
//            _subjectsErrorMessage.value = null
//            Log.d("AttendanceQrVM", "Attempting to fetch subjects for teacher ID: $teacherId")
//            when (val result = subjectRepository.getAllSubjects()) {
//                is Resource.Success -> {
//                    // Filter subjects by the current teacher's ID
//                    val teacherSubjects = result.data.filter { it.teacher?.id == teacherId }
//                    _subjects.value = teacherSubjects
//                    _isSubjectsLoading.value = false
//                    if (teacherSubjects.isEmpty()) {
//                        _subjectsErrorMessage.value = "No subjects assigned to you."
//                    }
//                    Log.d("AttendanceQrVM", "Subjects fetched: ${teacherSubjects.size} subjects.")
//                }
//                is Resource.Error -> {
//                    val errorMessage = result.message ?: "Failed to load subjects."
//                    _subjectsErrorMessage.value = errorMessage
//                    _isSubjectsLoading.value = false
//                    _subjects.value = emptyList()
//                    Log.e("AttendanceQrVM", "Error fetching subjects: $errorMessage")
//                }
//                is Resource.Loading -> { /* Handled by _isSubjectsLoading */ }
//            }
//        }
//    }
//
//
//    /**
//     * Teacher Function: Generates an AttendanceToken and updates QR code data.
//     * @param subject The subject for which the attendance token is being generated.
//     */
//    fun generateAttendanceTokenForQr(subject: Subject) {
//        viewModelScope.launch {
//            _teacherMessage.value = null
//            _qrCodeData.value = null // Clear previous QR data
//            if (!_isCurrentUserTeacher.value) { // Use the observed StateFlow value
//                _teacherMessage.value = "Error: Only teachers can generate QR codes."
//                return@launch
//            }
//            if (_currentUserId.value == 0L) { // Use the observed StateFlow value
//                _teacherMessage.value = "Error: Teacher ID not available."
//                return@launch
//            }
//
//            try {
//                val generatedAt = System.currentTimeMillis()
//                val expiresAt = generatedAt + (5 * 60 * 1000L) // 5 minutes expiry
//
//
//                val zoneId = ZoneId.systemDefault()
//                val generatedTimestamp = Instant.ofEpochMilli(generatedAt)
//                    .atZone(zoneId)
//                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) // ✅ ISO-8601 format
//
//                val expiresTimestamp = Instant.ofEpochMilli(expiresAt)
//                    .atZone(zoneId)
//                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
//
//                val newToken = AttendanceTokenReq(
//                    subjectId = subject.id
//                )
//                val response = attendanceTokenApiService.createAttendanceToken(newToken)
//                if (response.isSuccessful && response.body() != null) {
//                    val createdToken = response.body()!!
//                    _qrCodeData.value = Gson().toJson(createdToken)
//                    _teacherMessage.value = "QR Code generated successfully for ${createdToken.subject.name}!"
//                    Log.d("AttendanceQrVM", "Generated QR: ${_qrCodeData.value}")
//                } else {
//                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
//                    _teacherMessage.value = "Failed to generate attendance token: $errorMsg"
//                    Log.e("AttendanceQrVM", "API error generating token: $errorMsg")
//                }
//            } catch (e: Exception) {
//                _teacherMessage.value = "Error generating attendance token: ${e.message}"
//                Log.e("AttendanceQrVM", "Exception generating token", e)
//            }
//        }
//    }
//
//    /**
//     * Student Function: Processes scanned QR code content.
//     * @param content The string content extracted from the QR code.
//     */
//    fun onQrCodeScanned(content: String) {
//        _scannedQrContent.value = content
//        _attendanceMarkingStatus.value = AttendanceMarkingStatus.Idle
//        Log.d("AttendanceQrVM", "QR Code scanned: $content")
//    }
//
//    /**
//     * Student Function: Confirms and marks attendance after scanning.
//     * @param attendanceToken The AttendanceToken parsed from the QR code.
//     */
//    fun markAttendance(attendanceToken: AttendanceToken) {
//        viewModelScope.launch {
//            _attendanceMarkingStatus.value = AttendanceMarkingStatus.Loading
//
//            val student = _studentLoginResponse.value
//            if (student == null) {
//                _attendanceMarkingStatus.value = AttendanceMarkingStatus.Error("Student information not available. Please try logging in again.")
//                Log.e("AttendanceQrVM", "markAttendance: StudentLoginResponse is null.")
//                return@launch
//            }
//
//            val currentTimeMillis = System.currentTimeMillis().toString()
//            val expiresAtMillis = attendanceToken.expiresAt
//            val generatedAtMillis = attendanceToken.generatedAt
//            if (currentTimeMillis > expiresAtMillis) {
//                _attendanceMarkingStatus.value = AttendanceMarkingStatus.Error("QR Code has expired!")
//                Log.w("AttendanceQrVM", "QR Code expired. Current: $currentTimeMillis, Expires: ${attendanceToken.expiresAt}")
//                return@launch
//            }
////            if (currentTimeMillis < generatedAtMillis) {
////                _attendanceMarkingStatus.value = AttendanceMarkingStatus.Error("QR code is not yet active.")
////                Log.w("AttendanceQrVM", "QR Code not active yet. Current: $currentTimeMillis, Generated: ${attendanceToken.generatedAt}")
////                return@launch
////            }
//
//            val attendance = AttendanceReq(
//                studentId = student.id,
//                subjectId = attendanceToken.subject.id,
//                status = "Present"
//            )
//            Log.d("AttendanceQrVM", "Attempting to mark attendance for student ${student.id} in subject ${attendanceToken.subject.name}")
//
//            try {
//                val response = attendanceApiService.createAttendance(attendance)
//                if (response.isSuccessful && response.body() != null) {
//                    _attendanceMarkingStatus.value = AttendanceMarkingStatus.Success("Attendance marked successfully for ${attendanceToken.subject.name}!")
//                    Log.d("AttendanceQrVM", "Attendance marked successfully. Response: ${response.body()}")
//                } else {
//                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
//                    _attendanceMarkingStatus.value = AttendanceMarkingStatus.Error("Failed to mark attendance: $errorMsg")
//                    Log.e("AttendanceQrVM", "API error marking attendance: $errorMsg")
//                }
//            } catch (e: Exception) {
//                _attendanceMarkingStatus.value = AttendanceMarkingStatus.Error("Error marking attendance: ${e.message}")
//                Log.e("AttendanceQrVM", "Exception marking attendance", e)
//            }
//        }
//    }
//
//    fun resetTeacherMessage() {
//        _teacherMessage.value = null
//    }
//
//    fun resetScannedContentAndStatus() {
//        _scannedQrContent.value = null
//        _attendanceMarkingStatus.value = AttendanceMarkingStatus.Idle
//        Log.d("AttendanceQrVM", "Resetting scanned content and status.")
//    }
//}
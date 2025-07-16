package com.example.campussync.persentation.attendance.subject


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campussync.data.model.attendance.Attendance
import com.example.campussync.data.model.attendance.AttendanceReq
import com.example.campussync.data.model.student.StudentLoginResponse
import com.example.campussync.data.repository.*
import com.example.campussync.utils.Resource
import com.example.campussync.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter // Import for date formatting
import javax.inject.Inject

// Re-using data classes and repositories (assuming they are defined elsewhere and correctly injected)
// data class Student(val id: Long, val name: String, val studentUid: String, val email: String, val semester: Int)
// data class Teacher(val id: Long, val name: String, val email: String, val department: String)
// data class Subject(val id: Long, val name: String, val code: String, val semester: Int, val department: Department, val teacher: TeacherLoginResponse)
// data class Department(val id: Long, val name: String) // Assuming you have this
// data class Attendance(val id: Long, val student: StudentLoginResponse, val subject: Subject, val date: String, val status: String)
// data class StudentLoginResponse(...) // Your provided structure
// data class TeacherLoginResponse(...) // Your provided structure
// sealed class Resource<out T> { ... }
// class StudentRepository { suspend fun getStudentById(id: Long): Resource<Student> } // Assumed to return 'Student'
// class TeacherRepository { suspend fun getTeacherById(id: Long): Resource<Teacher> }
// class SubjectRepository { suspend fun getAllSubjects(): Resource<List<Subject>> }
// class AttendanceRepository {
//     suspend fun getAttendanceBySubjectId(subjectId: Long): Resource<List<Attendance>>
//     suspend fun createAttendance(attendance: Attendance): Resource<Attendance> // This is the new method
// }
// class UserPreferences {
//     val userId: Flow<String?>
//     val isTeacher: Flow<Boolean>
// }

// Your UiState and AttendanceRecordItem as defined previously
data class SubjectAttendanceUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val subjectName: String = "",
    val subjectId: Long = 0L,
    val detailedAttendance: List<AttendanceRecordItem> = emptyList(),
    val selectedMonth: Int = LocalDate.now().monthValue,
    val isMarkingAttendance: Boolean = false,
    val markAttendanceResult: String? = null,
    val isGeneratingQr: Boolean = false,
    val qrCodeData: String? = null,
    val currentUserId: Long = 0L,
    val isCurrentUserTeacher: Boolean = false,
    val currentStudent: StudentLoginResponse? = null // Add to hold current student's full data
)

data class AttendanceRecordItem(
    val date: LocalDate,
    val status: String,
    val studentName: String? = null,
    val studentId: Long? = null
)


@HiltViewModel
class SubjectAttendanceViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val studentRepository: StudentRepository,
    private val teacherRepository: TeacherRepository,
    private val subjectRepository: SubjectRepository,
    private val attendanceRepository: AttendanceRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectAttendanceUiState())
    val uiState: StateFlow<SubjectAttendanceUiState> = _uiState.asStateFlow()

    private val subjectId: Long = savedStateHandle.get<Long>("subjectId") ?: 0L

    init {
        _uiState.update { it.copy(subjectId = subjectId) }

        viewModelScope.launch {
            combine(userPreferences.userId, userPreferences.isTeacher) { userId, isTeacher ->
                val idLong = userId?.toLongOrNull() ?: 0L
                idLong to isTeacher
            }.collectLatest { (currentUserId, isCurrentUserTeacher) ->
                // Fetch student details if user is a student
                var studentLoginResponse: StudentLoginResponse? = null
                if (!isCurrentUserTeacher && currentUserId != 0L) {
                    when (val studentRes = studentRepository.getStudentById(currentUserId)) {
                        is Resource.Success -> {
                            // Assuming getStudentById returns a Student, you need to convert it to StudentLoginResponse
                            // or have getStudentById return StudentLoginResponse directly.
                            // For now, let's manually map if 'Student' is different from 'StudentLoginResponse'
                            val student = studentRes.data
                            studentLoginResponse = StudentLoginResponse(
                                id = student.id,
                                name = student.name,
                                studentUid = student.studentUid,
                                email = student.email,
                                token = "", // Token usually comes from login, not stored here for profile
                                semester = student.semester
                            )
                        }
                        is Resource.Error -> Log.e("SubjectAttendanceVM", "Error fetching student details: ${studentRes.message}")
                        is Resource.Loading -> {}
                    }
                }

                _uiState.update {
                    it.copy(
                        currentUserId = currentUserId,
                        isCurrentUserTeacher = isCurrentUserTeacher,
                        currentStudent = studentLoginResponse // Store full student data
                    )
                }
                if (subjectId != 0L && currentUserId != 0L) {
                    fetchSubjectAttendanceData(subjectId, currentUserId, isCurrentUserTeacher)
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Invalid subject or user ID.") }
                }
            }
        }
    }

    private fun fetchSubjectAttendanceData(subjectId: Long, currentUserId: Long, isCurrentUserTeacher: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val subjectResult = subjectRepository.getAllSubjects() // Get all subjects to find the specific one
            val attendanceRecordsResult = attendanceRepository.getAttendanceBySubjectId(subjectId)

            if (subjectResult is Resource.Success && attendanceRecordsResult is Resource.Success) {
                val subject = subjectResult.data.find { it.id == subjectId }
                if (subject == null) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Subject details not found.") }
                    return@launch
                }

                val filteredAndMappedRecords = attendanceRecordsResult.data.mapNotNull { record ->
                    if (!isCurrentUserTeacher && record.student.id != currentUserId) {
                        null
                    } else {
                        AttendanceRecordItem(
                            date = LocalDate.parse(record.date, DateTimeFormatter.ISO_LOCAL_DATE), // Parse date string
                            status = record.status,
                            studentName = if (isCurrentUserTeacher) record.student.name else null,
                            studentId = if (isCurrentUserTeacher) record.student.id else null
                        )
                    }
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        subjectName = subject.name,
                        detailedAttendance = filteredAndMappedRecords,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = (subjectResult as? Resource.Error)?.message
                            ?: (attendanceRecordsResult as? Resource.Error)?.message
                            ?: "Failed to load detailed attendance."
                    )
                }
            }
        }
    }

    fun updateSelectedMonth(month: Int) {
        _uiState.update { it.copy(selectedMonth = month) }
    }

    /**
     * Student: Marks attendance for the current user and screen's subject.
     * Uses createAttendance method from AttendanceRepository.
     */
    fun markAttendance() {
        viewModelScope.launch {
            _uiState.update { it.copy(isMarkingAttendance = true, markAttendanceResult = null) }
            val currentUserId = uiState.value.currentUserId
            val currentSubjectId = uiState.value.subjectId
            val currentStudent = uiState.value.currentStudent // Get the full student object
            val isCurrentUserTeacher = uiState.value.isCurrentUserTeacher

            if (currentUserId == 0L || currentSubjectId == 0L || currentStudent == null || isCurrentUserTeacher) {
                _uiState.update { it.copy(isMarkingAttendance = false, markAttendanceResult = "Error: Invalid user, subject ID, or not a student.") }
                return@launch
            }

            // Fetch the Subject object
            val subjectFetchResult = subjectRepository.getAllSubjects()
            val targetSubject = if (subjectFetchResult is Resource.Success) {
                subjectFetchResult.data.find { it.id == currentSubjectId }
            } else {
                null
            }

            if (targetSubject == null) {
                _uiState.update { it.copy(isMarkingAttendance = false, markAttendanceResult = "Error: Subject not found for attendance.") }
                return@launch
            }

            val attendanceToCreate = AttendanceReq(
                studentId = currentStudent.id,
                subjectId = targetSubject.id,
                status = "Present".uppercase() // Assuming direct mark attendance means present
            )

            val result = attendanceRepository.createAttendance(attendanceToCreate)

            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isMarkingAttendance = false, markAttendanceResult = "Attendance marked successfully!") }
                    // Re-fetch detailed attendance to update the view
                    fetchSubjectAttendanceData(currentSubjectId, currentUserId, isCurrentUserTeacher)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isMarkingAttendance = false, markAttendanceResult = "Error marking attendance: ${result.message}") }
                }
                is Resource.Loading -> { /* Do nothing here */ }
            }
        }
    }

    /**
     * Teacher: Generates a QR code for attendance for the screen's subject.
     */
    fun generateQrCode() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingQr = true, qrCodeData = null) }
            val currentTeacherId = uiState.value.currentUserId
            val currentSubjectId = uiState.value.subjectId

            if (currentTeacherId == 0L || currentSubjectId == 0L) {
                _uiState.update { it.copy(isGeneratingQr = false, errorMessage = "Error: Invalid teacher or subject ID.") }
                return@launch
            }

            // Simulate API call for QR code data
            kotlinx.coroutines.delay(1000) // Simulate network delay
            val simulatedQrData = "ATTENDANCE_CODE_SUB_${currentSubjectId}_TCH_${currentTeacherId}_${System.currentTimeMillis()}"

            _uiState.update { it.copy(isGeneratingQr = false, qrCodeData = simulatedQrData) }
            Log.d("SubjectAttendanceViewModel", "QR Code Generated: $simulatedQrData")
        }
    }

    fun clearMarkAttendanceResult() {
        _uiState.update { it.copy(markAttendanceResult = null) }
    }

    fun clearQrCodeData() {
        _uiState.update { it.copy(qrCodeData = null) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
package com.example.campussync.persentation.attendance


import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campussync.data.model.Attendance
import com.example.campussync.data.model.Subject
import com.example.campussync.data.repository.AttendanceRepository
import com.example.campussync.data.repository.StudentRepository
import com.example.campussync.data.repository.SubjectRepository
import com.example.campussync.data.repository.TeacherRepository
import com.example.campussync.data.repository.UserRepository
import com.example.campussync.utils.Resource
import com.example.campussync.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest // Important for collecting from flows
//region: Assuming these are your data classes and repositories
// Make sure to define them if you haven't already
// data class Attendance(...)
// data class Subject(...)
// data class Student(...) // Should have an 'id' and 'semester'
// data class Teacher(...) // Should have an 'id' and 'name'
// data class AttendanceCourseItem(val courseName: String, val lecturer: String, val percent: Int, val color: Color)
// enum class AttendanceFilter { MONTH, YEAR, ALL } // Example filters
// sealed class Resource<out T> { ... } // Your Resource class for API calls
// class StudentRepository { suspend fun getStudentById(id: Long): Resource<Student> }
// class TeacherRepository { /* potentially for teacher-specific data */ }
// class SubjectRepository { suspend fun getAllSubjects(): Resource<List<Subject>> }
// class AttendanceRepository { suspend fun getAllAttendance(): Resource<List<Attendance>> }
// class UserPreferences {
//     val isLoggedIn: Flow<Boolean>
//     val userId: Flow<String?>
//     val isTeacher: Flow<Boolean>
// }
//endregion

data class AttendanceUiState(
    val overallPercent: Double = 0.0,
    val selectedFilter: AttendanceFilter = AttendanceFilter.MONTH,
    val courses: List<AttendanceCourseItem> = emptyList(),
    val attendance: List<Attendance> = emptyList(),
    val subjects: List<Subject> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
    // Removed isTeacher, id, isLoggedIn as they are sourced from UserPreferences
)

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val teacherRepository: TeacherRepository, // Injected but not used in current logic, keep if needed elsewhere
    private val subjectRepository: SubjectRepository,
    private val attendanceRepository: AttendanceRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _attendanceUiState = MutableStateFlow(AttendanceUiState())
    val attendanceUiState: StateFlow<AttendanceUiState> = _attendanceUiState.asStateFlow()

    // Expose these directly from UserPreferences, collected into StateFlows
    // for easy consumption in Compose UI.
    val isLoggedIn: StateFlow<Boolean> = userPreferences.isLoggedIn.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Keep subscription for 5 seconds after last collector
        initialValue = false
    )

    val userId: StateFlow<String?> = userPreferences.userId.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val isTeacher: StateFlow<Boolean> = userPreferences.isTeacher.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    init {
        // Log current user preferences values on ViewModel init
        Log.d("AttendanceViewModel", "UserPrefs on init: User ID: ${userId.value}, Is Teacher: ${isTeacher.value}, Is Logged In: ${isLoggedIn.value}")

        // Collect user preferences to react to changes and trigger data loading
        viewModelScope.launch {
            // Use combine if you need both userId and isTeacher to decide when to load data
            // For simplicity, we'll collect userId and check isTeacher when userId is available.
            userId.collectLatest { idString ->
                val currentIsTeacher = isTeacher.value // Get the current value of isTeacher
                Log.d("AttendanceViewModel", "User ID collected: ${idString}, Is Teacher: $currentIsTeacher")

                // Only proceed if a valid userId is available and the user is a student (if getData is for student)
                // Or if the user is a teacher and you want to fetch teacher-specific data here.
                if (!idString.isNullOrBlank() && !currentIsTeacher) { // Assuming getData is for students
                    val studentId = idString.toLongOrNull()
                    if (studentId != null && studentId != 0L) {
                        Log.d("AttendanceViewModel", "Fetching student data for ID: $studentId")
                        getData(studentId)
                    } else {
                        Log.w("AttendanceViewModel", "Invalid student ID: $idString, skipping data fetch.")
                        // Potentially update UI state to indicate no data for invalid ID
                        _attendanceUiState.update { it.copy(isLoading = false, errorMessage = "Invalid user ID.") }
                    }
                } else if (currentIsTeacher) {
                    // TODO: Implement logic to fetch teacher-specific attendance data here
                    // If teachers view attendance differently or need different data.
                    Log.d("AttendanceViewModel", "User is a teacher. Implement teacher attendance data fetch.")
                    _attendanceUiState.update { it.copy(isLoading = false) } // Stop loading if no teacher data fetch implemented yet
                } else {
                    Log.d("AttendanceViewModel", "User not logged in or ID not available yet. Waiting...")
                }
            }
        }
    }

    /**
     * Fetches and processes attendance data for a given student.
     * This function should ideally be called when a valid studentId is confirmed.
     * @param studentId The ID of the student for whom to fetch attendance data.
     */
    private fun getData(studentId: Long) {
        viewModelScope.launch {
            _attendanceUiState.update { it.copy(isLoading = true, errorMessage = null) } // Reset error on new data fetch

            // Fetch all necessary data concurrently
            val subjectsResult = subjectRepository.getAllSubjects()
            val attendanceResult = attendanceRepository.getAllAttendance()
            val studentResult = studentRepository.getStudentById(studentId)

            // Check results from all repositories
            when {
                subjectsResult is Resource.Success && attendanceResult is Resource.Success && studentResult is Resource.Success -> {
                    val subjectsList = subjectsResult.data
                    val attendanceList = attendanceResult.data
                    val studentSemester = studentResult.data.semester
                    Log.d(
                        "AttendanceViewModel",
                        "Student ID: ${studentResult.data.id}, Semester: $studentSemester, Subjects: ${subjectsList.size}, Attendance: ${attendanceList.size}"
                    )

                    // Map subjects to attendance items for UI display
                    val courseItems = mapSubjectsToAttendanceItems(
                        subjects = subjectsList,
                        attendanceList = attendanceList,
                        studentId = studentId,
                        semester = studentSemester
                    )

                    // Calculate overall attendance percentage across all subjects for the student
                    val totalAttendance = courseItems.fold(0 to 0) { acc, item ->
                        val (present, total) = calculateStudentAttendance(
                            attendanceList = attendanceList,
                            studentId = studentId,
                            subjectId = subjectsList.find { it.name == item.courseName }?.id ?: 0,
                            semester = studentSemester
                        )
                        (acc.first + present) to (acc.second + total)
                    }

                    val overallPercent = if (totalAttendance.second == 0) {
                        0.0
                    } else {
                        (totalAttendance.first.toDouble() / totalAttendance.second) * 100
                    }

                    // Update UI state with fetched and processed data
                    _attendanceUiState.update {
                        it.copy(
                            overallPercent = overallPercent,
                            selectedFilter = AttendanceFilter.MONTH, // Default filter
                            isLoading = false,
                            errorMessage = null,
                            courses = courseItems,
                            attendance = attendanceList,
                            subjects = subjectsList
                        )
                    }
                }
                else -> {
                    Log.d(
                        "AttendanceViewModel",
                        "Error fetching data: Subjects: ${subjectsResult is Resource.Error}, Attendance: ${attendanceResult is Resource.Error}, Student: ${studentResult is Resource.Error}"
                    )
                    Log.d(
                        "AttendanceViewModel", //Subjects: ${(subjectsResult as? Resource.Error)?.message}, Attendance: ${(attendanceResult as? Resource.Error)?.message},
                        "Error messages: Student: ${(studentResult as? Resource.Error)?.message}"
                    )
                    // Handle errors if any of the data fetches fail
                    _attendanceUiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = (subjectsResult as? Resource.Error)?.message
                                ?: (attendanceResult as? Resource.Error)?.message
                                ?: (studentResult as? Resource.Error)?.message
                                ?: "Failed to load attendance data. Please try again." // Generic error
                        )
                    }
                }
            }
        }
    }

    /**
     * Calculates the number of present lectures and total lectures for a specific student and subject.
     * @param attendanceList List of all attendance records.
     * @param studentId The ID of the student.
     * @param subjectId The ID of the subject.
     * @param semester The semester of the student.
     * @return A Pair where first is present count and second is total count.
     */
    fun calculateStudentAttendance(
        attendanceList: List<Attendance>,
        studentId: Long,
        subjectId: Long,
        semester: Int
    ): Pair<Int, Int> {
        val subjectAttendance = attendanceList.filter {
            // Filter attendance records relevant to the specific subject and semester
            it.subject.id == subjectId && it.subject.semester == semester
        }
        val studentAttendanceForSubject = subjectAttendance.filter {
            // Further filter for the specific student within that subject and semester
            it.student.id == studentId && it.student.semester == semester
        }
        val presentCount = studentAttendanceForSubject.count {
            it.status.equals("Present", ignoreCase = true)
        }
        return presentCount to subjectAttendance.size // Total lectures for that subject in that semester
    }

    /**
     * Calculates the attendance percentage for a specific student and subject.
     * @param attendanceList List of all attendance records.
     * @param studentId The ID of the student.
     * @param subjectId The ID of the subject.
     * @param semester The semester of the student.
     * @return The attendance percentage (0.0 if total is zero).
     */
    fun getAttendancePercentage(
        attendanceList: List<Attendance>,
        studentId: Long,
        subjectId: Long,
        semester: Int
    ): Double {
        val (present, total) = calculateStudentAttendance(attendanceList, studentId, subjectId, semester)
        return if (total == 0) 0.0 else (present.toDouble() / total) * 100
    }

    /**
     * Maps a list of subjects to a list of AttendanceCourseItem for UI display.
     * Calculates attendance percentage for each subject for the given student.
     * @param subjects List of all subjects.
     * @param attendanceList List of all attendance records.
     * @param studentId The ID of the student.
     * @param semester The semester of the student.
     * @return List of AttendanceCourseItem.
     */
    fun mapSubjectsToAttendanceItems(
        subjects: List<Subject>,
        attendanceList: List<Attendance>,
        studentId: Long,
        semester: Int
    ): List<AttendanceCourseItem> {
        return subjects
            .filter { it.semester == semester } // Only include subjects for the student's semester
            .map { subject ->
                val percentage = getAttendancePercentage(
                    attendanceList = attendanceList,
                    studentId = studentId,
                    subjectId = subject.id,
                    semester = semester
                )
                AttendanceCourseItem(
                    courseName = subject.name,
                    lecturer = subject.teacher?.name ?: "Unknown", // Assuming Subject has a 'teacher' property
                    percent = percentage.toInt(),
                    color = randomBrightColor()
                )
            }
    }

    private val random = Random.Default

    /**
     * Generates a random bright color for UI elements.
     * @return A Compose Color object.
     */
    fun randomBrightColor(): Color {
        val colors = listOf(
            Color(255, 99, 71),  // Tomato
            Color(60, 179, 113), // MediumSeaGreen
            Color(106, 90, 205), // SlateBlue
            Color(255, 215, 0)   // Gold
        )
        return colors[random.nextInt(colors.size)]
    }

    /**
     * Updates the selected filter in the UI state.
     * @param filter The new AttendanceFilter to set.
     */
    fun updateSelectedFilter(filter: AttendanceFilter) {
        _attendanceUiState.update { it.copy(selectedFilter = filter) }
        // TODO: If changing filter requires re-fetching or re-processing data,
        //  call the appropriate function here.
        //  e.g., if filter changes from MONTH to YEAR, you might need to
        //  re-calculate percentages based on a different time range.
    }

    // Removed the following functions as their state is now managed by UserPreferences:
    // fun updateIsTeacher(isTeacher: Boolean) { ... }
    // fun updateId(id: Long) { ... }
    // fun updateIsLoggedIn(isLoggedIn: Boolean) { ... }
}
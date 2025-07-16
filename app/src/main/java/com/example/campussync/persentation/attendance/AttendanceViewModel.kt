package com.example.campussync.persentation.attendance


import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campussync.data.model.attendance.Attendance
import com.example.campussync.data.model.Subject
import com.example.campussync.data.model.teacher.TeacherLoginResponse
import com.example.campussync.data.repository.AttendanceRepository
import com.example.campussync.data.repository.EnrollmentRepository
import com.example.campussync.data.repository.StudentRepository
import com.example.campussync.data.repository.SubjectRepository
import com.example.campussync.data.repository.TeacherRepository
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
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

// Data classes for UI representation
data class AttendanceCourseItem(
    val courseName: String,
    val lecturer: String,
    val percent: Int,
    val color: Color
)

enum class AttendanceFilter {
    MONTH, SEMESTER, YEAR, ALL
}

data class AttendanceUiState(
    val overallPercent: Double = 0.0, // More relevant for student view
    val selectedFilter: AttendanceFilter = AttendanceFilter.MONTH,
    val courses: List<AttendanceCourseItem> = emptyList(), // Can be used for both, representing subjects
    val attendance: List<Attendance> = emptyList(), // Raw attendance records (could be specific to student/subject)
    val subjects: List<Subject> = emptyList(), // All subjects, for context or teacher's subjects
    val currentTeacher: TeacherLoginResponse? = null, // For teacher's data
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val teacherSubjectAttendanceSummary: Map<Long, TeacherSubjectAttendanceSummary> = emptyMap() // For teacher's view
)

data class TeacherSubjectAttendanceSummary(
    val subjectId: Long,
    val subjectName: String,
    val totalStudents: Int, // Total unique students recorded for this subject
    val presentStudents: Int, // Total 'Present' records for this subject
    val overallClassPercentage: Double, // Overall class percentage for this subject
    val latestAttendanceDate: String? = null // To show when attendance was last marked for this class
)


@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val teacherRepository: TeacherRepository,
    private val subjectRepository: SubjectRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val attendanceRepository: AttendanceRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _attendanceUiState = MutableStateFlow(AttendanceUiState())
    val attendanceUiState: StateFlow<AttendanceUiState> = _attendanceUiState.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = userPreferences.isLoggedIn.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
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
        Log.d("AttendanceViewModel", "UserPrefs on init: User ID: ${userId.value}, Is Teacher: ${isTeacher.value}, Is Logged In: ${isLoggedIn.value}")

        viewModelScope.launch {
            combine(userId, isTeacher, isLoggedIn) { idString, isTeacherFlag, isLoggedInFlag ->
                Triple(idString?.toLongOrNull() ?: 0L, isTeacherFlag, isLoggedInFlag)
            }.collectLatest { (currentUserId, currentIsTeacher, currentIsLoggedIn) ->
                Log.d("AttendanceViewModel", "Combined UserPrefs collected: ID=$currentUserId, Teacher=$currentIsTeacher, LoggedIn=$currentIsLoggedIn")

                if (currentIsLoggedIn && currentUserId != 0L) {
                    if (currentIsTeacher) {
                        Log.d("AttendanceViewModel", "User is a teacher. Fetching teacher data for ID: $currentUserId")
                        getTeacherData(currentUserId)
                    } else {
                        Log.d("AttendanceViewModel", "User is a student. Fetching student data for ID: $currentUserId")
                        getStudentData(currentUserId)
                    }
                } else {
                    Log.d("AttendanceViewModel", "User not logged in or ID not available. Resetting state.")
                    _attendanceUiState.update { AttendanceUiState() }
                }
            }
        }
    }

    /**
     * Fetches and processes attendance data for a given student.
     * Uses optimized API call to fetch attendance specific to the student.
     * @param studentId The ID of the student for whom to fetch attendance data.
     */
    private fun getStudentData(studentId: Long) {
        viewModelScope.launch {
            _attendanceUiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Fetch only student's attendance records
            val studentAttendanceResult = attendanceRepository.getAttendanceByStudentId(studentId)
            val enrollmentsResult = enrollmentRepository.getEnrollmentsByStudentId(studentId)
            val subjectsResult = subjectRepository.getAllSubjects() // Still need all subjects for mapping course names/lecturers
            val studentResult = studentRepository.getStudentById(studentId)

            when {
                studentAttendanceResult is Resource.Success && subjectsResult is Resource.Success && studentResult is Resource.Success && enrollmentsResult is Resource.Success-> {
                    val attendanceList = studentAttendanceResult.data // This is already student-specific
                    val subjectsList = subjectsResult.data
                    val enrollmentsList = enrollmentsResult.data

                    val studentSemester = studentResult.data.semester
                    Log.d("AttendanceViewModel", "Student Data fetched. Attendance records: ${attendanceList.size}, Subjects: ${subjectsList.size}")

                    // Now, filter subjects relevant to the student's semester to create course items
                    val enrolledSubjectIds = enrollmentsList.map { it.subject.id }.toSet()
                    val studentRelevantSubjects = subjectsList.filter { it.id in enrolledSubjectIds }

                    val courseItems = mapSubjectsToAttendanceItems(
                        subjects = studentRelevantSubjects, // Pass only relevant subjects
                        attendanceList = attendanceList, // This attendance list is already for the student
                        studentId = studentId,
                        semester = studentSemester
                    )

                    val totalAttendance = courseItems.fold(0 to 0) { acc, item ->
                        // When calculating overall, we are still operating on student-specific attendance
                        val subjectMap = studentRelevantSubjects.associateBy { it.name }
                        val subjectIdForCourse = subjectMap[item.courseName]?.id ?: 0
                        val (present, total) = calculateStudentAttendance(
                            attendanceList = attendanceList, // Use the student-specific list
                            studentId = studentId,
                            subjectId = subjectIdForCourse,
                            semester = studentSemester
                        )
                        (acc.first + present) to (acc.second + total)
                    }

                    val overallPercent = if (totalAttendance.second == 0) {
                        0.0
                    } else {
                        (totalAttendance.first.toDouble() / totalAttendance.second) * 100
                    }

                    _attendanceUiState.update {
                        it.copy(
                            overallPercent = overallPercent,
                            selectedFilter = AttendanceFilter.MONTH,
                            isLoading = false,
                            errorMessage = null,
                            courses = courseItems,
                            attendance = attendanceList, // Store student's attendance
                            subjects = subjectsList, // Store all subjects for potential future use or broader context
                            currentTeacher = null,
                            teacherSubjectAttendanceSummary = emptyMap()
                        )
                    }
                }
                else -> {
                    val errorMsg = (studentAttendanceResult as? Resource.Error)?.message
                        ?: (subjectsResult as? Resource.Error)?.message
                        ?: (studentResult as? Resource.Error)?.message
                        ?: "Failed to load student attendance data. Please check your connection."
                    Log.e("AttendanceViewModel", "Error loading student data: $errorMsg")
                    _attendanceUiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = errorMsg
                        )
                    }
                }
            }
        }
    }

    /**
     * Fetches and processes data for a given teacher.
     * Uses optimized API calls to fetch attendance specific to each subject.
     * @param teacherId The ID of the teacher.
     */
    private fun getTeacherData(teacherId: Long) {
        viewModelScope.launch {
            _attendanceUiState.update { it.copy(isLoading = true, errorMessage = null) }

            val teacherResult = teacherRepository.getTeacherById(teacherId)
            val subjectsResult = subjectRepository.getAllSubjects() // Get all subjects to filter for teacher's subjects

            when {
                teacherResult is Resource.Success && subjectsResult is Resource.Success -> {
                    val currentTeacher = teacherResult.data
                    val allSubjects = subjectsResult.data

                    val teacherSubjects = allSubjects.filter { it.teacher?.id == currentTeacher.id }
                    Log.d("AttendanceViewModel", "Teacher ${currentTeacher.name} teaches ${teacherSubjects.size} subjects.")

                    val summaryMap = mutableMapOf<Long, TeacherSubjectAttendanceSummary>()
                    val allTeacherRelatedAttendance = mutableListOf<Attendance>() // Collect all relevant attendance

                    teacherSubjects.forEach { subject ->
                        // Fetch attendance specifically for this subject
                        val attendanceForSubjectResult = attendanceRepository.getAttendanceBySubjectId(subject.id)

                        if (attendanceForSubjectResult is Resource.Success) {
                            val attendanceForSubject = attendanceForSubjectResult.data
                            allTeacherRelatedAttendance.addAll(attendanceForSubject) // Add to a collective list

                            val todayStudentsPresent = attendanceForSubject.count {
                                it.status.equals("Present", ignoreCase = true) && it.date.equals(LocalDate.now().toString())
                            }

                            val totalStudentsPresent = attendanceForSubject.count {
                                it.status.equals("Present", ignoreCase = true)
                            }

                            val totalStudents = attendanceForSubject.size

                            val uniqueStudentsAttended = attendanceForSubject.map { it.student.id }.distinct().size
                            val latestDate = attendanceForSubject.maxOfOrNull { it.date }

                            // Count distinct lecture instances for this subject
                            val distinctLectureDates = attendanceForSubject.map { it.date }.distinct().size
                            val overallPercentage = if (distinctLectureDates == 0) {
                                0.0
                            } else {
                                // This assumes 'presentStudents' is the count of 'Present' entries
                                // and 'distinctLectureDates' is the total lectures
                                // This isn't a true class percentage (students present / total enrolled * lectures)
                                // but rather a density of 'Present' records vs. lectures.
                                // For accurate class percentage, you'd need student enrollment per subject per lecture.
                                (totalStudentsPresent.toDouble() / totalStudents) * 100 // Simplified for demo
                            }

                            summaryMap[subject.id] = TeacherSubjectAttendanceSummary(
                                subjectId = subject.id,
                                subjectName = subject.name,
                                totalStudents = uniqueStudentsAttended, // Number of unique students who have attended
                                presentStudents = todayStudentsPresent, // Total 'Present' records
                                overallClassPercentage = overallPercentage,
                                latestAttendanceDate = latestDate
                            )
                        } else if (attendanceForSubjectResult is Resource.Error) {
                            Log.e("AttendanceViewModel", "Error fetching attendance for subject ${subject.name}: ${attendanceForSubjectResult.message}")
                            // Optionally, add an entry to summaryMap indicating error or no data
                            summaryMap[subject.id] = TeacherSubjectAttendanceSummary(
                                subjectId = subject.id,
                                subjectName = subject.name,
                                totalStudents = 0,
                                presentStudents = 0,
                                overallClassPercentage = 0.0,
                                latestAttendanceDate = "Error loading"
                            )
                        }
                    }

                    _attendanceUiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null,
                            currentTeacher = currentTeacher,
                            subjects = teacherSubjects, // Only subjects taught by this teacher
                            attendance = allTeacherRelatedAttendance, // Collective attendance for teacher's subjects
                            teacherSubjectAttendanceSummary = summaryMap,
                            overallPercent = 0.0,
                            courses = emptyList()
                        )
                    }
                }
                else -> {
                    val errorMsg = (teacherResult as? Resource.Error)?.message
                        ?: (subjectsResult as? Resource.Error)?.message
                        ?: "Failed to load teacher data. Please check your connection."
                    Log.e("AttendanceViewModel", "Error loading teacher data: $errorMsg")
                    _attendanceUiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = errorMsg,
                            currentTeacher = null,
                            subjects = emptyList(),
                            teacherSubjectAttendanceSummary = emptyMap()
                        )
                    }
                }
            }
        }
    }

    /**
     * Calculates the number of present lectures and total lectures for a specific student and subject.
     * This function now expects `attendanceList` to be *already filtered* for the specific student.
     * @param attendanceList List of attendance records for the specific student.
     * @param studentId The ID of the student.
     * @param subjectId The ID of the subject.
     * @param semester The semester of the student.
     * @return A Pair where first is present count and second is total count.
     */
    fun calculateStudentAttendance(
        attendanceList: List<Attendance>, // This list should ideally already be for the student
        studentId: Long,
        subjectId: Long,
        semester: Int
    ): Pair<Int, Int> {
        val studentSubjectAttendance = attendanceList.filter {
            // Further filter for the specific subject and semester within the student's records
            it.subject.id == subjectId && it.student.id == studentId
        }
        val presentCount = studentSubjectAttendance.count {
            it.status.equals("PRESENT", ignoreCase = true)
        }
        // Total lectures for that subject in that semester attended by this student
        // Or, total lectures for that subject in that semester from the broader context
        // If the API returns only records for lectures the student attended, this is correct.
        // If it returns all lecture instances for the student, it needs adjustment.
        // For simplicity, assuming it returns only records for the student.
        // we can add the .distinct() function to make date distincts.
        return presentCount to studentSubjectAttendance.map { it.date }.size
    }

    /**
     * Calculates the attendance percentage for a specific student and subject.
     * This function now expects `attendanceList` to be *already filtered* for the specific student.
     * @param attendanceList List of attendance records for the specific student.
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
     * @param subjects List of subjects relevant to the student's semester.
     * @param attendanceList List of attendance records for the specific student.
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
            .map { subject ->
                Log.d("AttendanceViewModel", "Processing subject: ${subject.name}")
                val percentage = getAttendancePercentage(
                    attendanceList = attendanceList,
                    studentId = studentId,
                    subjectId = subject.id,
                    semester = semester
                )
                Log.d("AttendanceViewModel", "Subject: ${subject.name}, Percentage: $percentage")
                AttendanceCourseItem(
                    courseName = subject.name,
                    lecturer = subject.teacher?.name ?: "Unknown",
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
    }
}
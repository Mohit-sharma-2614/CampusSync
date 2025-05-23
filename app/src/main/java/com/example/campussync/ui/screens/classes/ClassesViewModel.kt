package com.example.campussync.ui.screens.classes

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.campussync.data.repository.AssignmentRepo
import com.example.campussync.data.repository.AttendanceRecordRepo
import com.example.campussync.data.repository.ClassSessionRepo
import com.example.campussync.data.repository.CourseRepo
import com.example.campussync.data.repository.SemesterRepo
import com.example.campussync.data.repository.StudentRepo
import com.example.campussync.ui.screens.utils.getDefaultNavItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ClassesViewModel(
    createSavedStateHandle: SavedStateHandle,
    studentRepo: StudentRepo,
    semesterRepo: SemesterRepo,
    courseRepo: CourseRepo,
    classSessionRepo: ClassSessionRepo,
    assignmentRepo: AssignmentRepo,
    attendanceRecordRepo: AttendanceRecordRepo
) : ViewModel() {
    private val _uiState = MutableStateFlow(ClassesUiState())
    val uiState: StateFlow<ClassesUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { currentState ->
            currentState.copy(
                days = listOf(
                    DaySchedule(
                        label = "Mon 20",
                        sessions = listOf(
                            ClassSession("Maths", "09:00", "10:00", "Dr. Sharma", "Room A101", Color(0xFFB39DDB)),
                            ClassSession("Physics", "10:15", "11:15", "Dr. Gupta", "Room B203", Color(0xFFFFAB91)),
                            ClassSession("Chemistry", "11:30", "12:30", "Dr. Mehta", "Room C202", Color(0xFFA5D6A7))
                        )
                    ),
                    DaySchedule(
                        label = "Tue 21",
                        sessions = listOf(
                            ClassSession("English", "09:00", "10:00", "Ms. Rao", "Room D101", Color(0xFF80CBC4)),
                            ClassSession("Maths", "10:15", "11:15", "Dr. Sharma", "Room A101", Color(0xFFB39DDB)),
                            ClassSession("CS", "12:00", "01:00", "Prof. Verma", "Lab L1", Color(0xFFEF9A9A))
                        )
                    ),
                    DaySchedule(
                        label = "Wed 22",
                        sessions = listOf(
                            ClassSession("Physics", "10:00", "11:00", "Dr. Gupta", "Room B203", Color(0xFFFFAB91)),
                            ClassSession("CS", "11:15", "12:15", "Prof. Verma", "Lab L1", Color(0xFFEF9A9A)),
                            ClassSession("Sports", "02:00", "04:00", "Coach Raj", "Playground", Color(0xFFB2EBF2))
                        )
                    ),
                    DaySchedule(
                        label = "Thu 23",
                        sessions = listOf(
                            ClassSession("Chemistry", "09:00", "10:00", "Dr. Mehta", "Room C202", Color(0xFFA5D6A7)),
                            ClassSession("English", "10:15", "11:15", "Ms. Rao", "Room D101", Color(0xFF80CBC4)),
                            ClassSession("Maths", "12:00", "01:00", "Dr. Sharma", "Room A101", Color(0xFFB39DDB))
                        )
                    ),
                    DaySchedule(
                        label = "Fri 24",
                        sessions = listOf(
                            ClassSession("Physics", "09:00", "10:00", "Dr. Gupta", "Room B203", Color(0xFFFFAB91)),
                            ClassSession("Lab", "10:15", "12:15", "Prof. Verma", "Lab L2", Color(0xFFCE93D8)),
                            ClassSession("Workshop", "01:30", "03:30", "Mr. Singh", "Room W1", Color(0xFFFFF59D))
                        )
                    ),
                    DaySchedule(
                        label = "Sat 25",
                        sessions = listOf(
                            ClassSession("Maths", "10:00", "11:00", "Dr. Sharma", "Room A101", Color(0xFFB39DDB)),
                            ClassSession("Group Discussion", "11:15", "12:15", "Ms. Rao", "Room G1", Color(0xFFFFCC80))
                        )
                    ),
                    DaySchedule(
                        label = "Sun 26",
                        sessions = listOf(
                            ClassSession("Revision", "09:00", "10:30", "Self Study", "Library", Color(0xFFB0BEC5)),
                            ClassSession("Project Work", "11:00", "01:00", "Self", "Lab L1", Color(0xFFFFAB91))
                        )
                    ),
                    DaySchedule(
                        label = "Mon 27",
                        sessions = listOf(
                            ClassSession("CS", "09:00", "10:00", "Prof. Verma", "Room A203", Color(0xFFEF9A9A)),
                            ClassSession("English", "10:15", "11:15", "Ms. Rao", "Room D101", Color(0xFF80CBC4))
                        )
                    ),
                    DaySchedule(
                        label = "Tue 28",
                        sessions = listOf(
                            ClassSession("Physics", "09:00", "10:00", "Dr. Gupta", "Room B203", Color(0xFFFFAB91)),
                            ClassSession("Workshop", "10:15", "12:15", "Mr. Singh", "Room W2", Color(0xFFFFF59D))
                        )
                    ),
                    DaySchedule(
                        label = "Wed 29",
                        sessions = listOf(
                            ClassSession("Chemistry", "09:00", "10:00", "Dr. Mehta", "Room C202", Color(0xFFA5D6A7)),
                            ClassSession("CS", "10:15", "11:15", "Prof. Verma", "Room A204", Color(0xFFEF9A9A)),
                            ClassSession("Maths", "11:30", "12:30", "Dr. Sharma", "Room A101", Color(0xFFB39DDB))
                        )
                    )
                ),
                navItems = getDefaultNavItems(),
                selectedNavIndex = 1
            )
        }
    }

    fun onNavItemSelected(index: Int) {
        _uiState.value = _uiState.value.copy(selectedNavIndex = index)
    }

    fun onDaySelected(index: Int) {
        _uiState.value = _uiState.value.copy(selectedDayIndex = index)
    }

//    fun onClassSelected(session: ClassSession) {
//        // TODO: Handle class selection
//    }


}



data class DaySchedule(
    val label: String, // e.g. "Mon 21"
    val sessions: List<ClassSession>
)

data class ClassSession(
    val title: String,
    val start: String,
    val end: String,
    val lecturer: String,
    val location: String,
    val color: Color,
    val isOngoing: Boolean = false,
    val progress: Float = 0f // 0..1 if ongoing
)
package com.example.campussync.ui.screens.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.campussync.R
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

class ProfileViewModel(
    savedStateHandle: SavedStateHandle,
    private val studentRepo: StudentRepo,
    private val semesterRepo: SemesterRepo,
    private val courseRepo: CourseRepo,
    private val classSessionRepo: ClassSessionRepo,
    private val assignmentRepo: AssignmentRepo,
    private val attendanceRecordRepo: AttendanceRecordRepo
): ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = ProfileUiState(
            name = "Mohit Kumar",
            avatar = R.drawable.ic_launcher_foreground,
            rollNumber = "19CS1234",
            branch = "Computer Science",
            semester = 6,
            attendancePercent = 82,
            cgpa = 8.24,
            creditsEarned = 120,
            quickActions = listOf(
                QuickAction("Schedule", Icons.Default.CalendarToday),
                QuickAction("Results", Icons.Default.Leaderboard),
                QuickAction("Contact", Icons.Default.Call)
            ),
            timeline = listOf(
                SemesterRecord(1, 8.0, 20, 20),
                SemesterRecord(2, 8.2, 40, 40),
                SemesterRecord(3, 8.1, 60, 60),
                SemesterRecord(4, 8.3, 80, 80),
                SemesterRecord(5, 8.2, 100, 100),
                SemesterRecord(6, 8.24, 120, 140)
            ),
            notifications = listOf(
                NotificationItem("2 assignments pending", "May 25", Icons.Default.Assignment),
                NotificationItem("Exam fees paid", "May 01", Icons.Default.ReceiptLong),
                NotificationItem("Next exam", "May 28", Icons.Default.Schedule)
            ),
            navItems = getDefaultNavItems(),
            selectedNavIndex = 2
        )
    }

    fun onNavItemSelected(index: Int) {
        _uiState.update { currentState ->
            currentState.copy(selectedNavIndex = index)
        }
    }


}
/* -------------------------------------------------- */
/* ---------------- DATA MODELS --------------------- */
/* -------------------------------------------------- */

data class QuickAction(val label: String, val icon: ImageVector)

data class SemesterRecord(
    val semester: Int,
    val gpa: Double,
    val creditsEarned: Int,
    val creditsTotal: Int
)

data class NotificationItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)
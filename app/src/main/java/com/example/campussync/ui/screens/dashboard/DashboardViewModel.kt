package com.example.campussync.ui.screens.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
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
import com.example.campussync.ui.navigation.Routes
import com.example.campussync.ui.screens.utils.getDefaultNavItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardViewModel(
    savedStateHandle: SavedStateHandle,
    private val studentRepo: StudentRepo,
    private val semesterRepo: SemesterRepo,
    private val courseRepo: CourseRepo,
    private val classSessionRepo: ClassSessionRepo,
    private val assignmentRepo: AssignmentRepo,
    private val attendanceRecordRepo: AttendanceRecordRepo
): ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _event = MutableStateFlow<String?>(null)
    val event: StateFlow<String?> = _event.asStateFlow()

    init {
        _uiState.value = DashboardUiState(
            cards = listOf(
                DashboardCard(
                    title = "Attendance",
                    iconRes = R.drawable.ic_launcher_foreground,
                    colors = listOf(Color(0xFF46A6FF), Color(0xFF4288FD)),
                    extra = "82%",
                    destination = Routes.ATTENDANCE_SCREEN
                ),
                DashboardCard(
                    title = "Schedule",
                    iconRes = R.drawable.ic_launcher_foreground,
                    colors = listOf(Color(0xFFAB47BC), Color(0xFF8E24AA)),
                    badge = 3,
                    destination = Routes.SCHEDULE_SCREEN
                ),
                DashboardCard(
                    title = "Assignments",
                    iconRes = R.drawable.ic_launcher_foreground,
                    colors = listOf(Color(0xFFFF7043), Color(0xFFF4511E)),
                    badge = 2,
                    destination = Routes.ASSIGNMENTS_SCREEN
                ),
                DashboardCard(
                    title = "Notes",
                    iconRes = R.drawable.ic_launcher_foreground,
                    colors = listOf(Color(0xFF66BB6A), Color(0xFF43A047)),
                    destination = Routes.NOTES_SCREEN
                ),
                DashboardCard(
                    title = "Chatbot",
                    iconRes = R.drawable.ic_launcher_foreground,
                    colors = listOf(Color(0xFF26C6DA), Color(0xFF00ACC1)),
                    destination = Routes.CHATBOT_SCREEN
                ),
                DashboardCard(
                    title = "Portal",
                    iconRes = R.drawable.ic_launcher_foreground,
                    colors = listOf(Color(0xFFFFEE58), Color(0xFFFDD835)),
                    destination = Routes.PORTAL_SCREEN
                )
            ),
            navItems = getDefaultNavItems(),
            selectedNavIndex = 0
        )
    }

    fun onNavItemSelected(index: Int) {
        _uiState.value = _uiState.value.copy(selectedNavIndex = index)
    }

}
/* ---------- STATE MODELS ---------- */

data class DashboardCard(
    val title: String,
    val iconRes: Int,
    val colors: List<Color>,
    val badge: Int? = null,
    val extra: String? = null,
    val destination: String
)

data class NavItem(
    val label: String,
    val icon: ImageVector
)
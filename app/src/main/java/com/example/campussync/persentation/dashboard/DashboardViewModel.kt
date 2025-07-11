package com.example.campussync.persentation.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.rounded.FileDownloadDone
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campussync.data.repository.StudentRepository
import com.example.campussync.data.repository.TeacherRepository
import com.example.campussync.data.repository.UserRepository
import com.example.campussync.navigation.AssignmentsRoute
import com.example.campussync.navigation.AttendanceRoute
import com.example.campussync.navigation.Routes
import com.example.campussync.navigation.dashboardRoutes
import com.example.campussync.utils.TokenManager
import com.example.campussync.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest // Crucial for collecting flows
import android.util.Log // For debugging

// Assuming these are defined elsewhere:
// data class Routes(...)
// val dashboardRoutes: List<Routes> = listOf(...)
// Your repository interfaces and TokenManager, UserPreferences, etc.

data class DashboardCard(
    val title: String,
    val iconRes: ImageVector,
    val colors: List<Color>,
    val badge: Int? = null,
    val extra: String? = null,
    val destination: String
)

data class DashboardUiState(
    val cards: List<DashboardCard> = emptyList(),
    val navItems: List<Routes> = emptyList(),
    val isTeacher: Boolean = false, // Will be updated from UserPreferences
    val id: Long = 0L,              // Will be updated from UserPreferences
    val selectedNavIndex: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val studentRepository: StudentRepository, // Keep if needed for student-specific dashboard data
    // private val userRepository: UserRepository, // Remove this if userPreferences fully replaces it
    private val teacherRepository: TeacherRepository, // Keep if needed for teacher-specific dashboard data
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    // _event can remain if you use it for one-shot UI events, otherwise it's redundant.
    private val _event = MutableStateFlow<String?>(null)
    val event: StateFlow<String?> = _event.asStateFlow()

    init {
        // Initialize dashboard cards with default/placeholder values.
        // Dynamic data like "82%" and `badge` should be fetched and updated asynchronously.
        _uiState.update { currentState ->
            currentState.copy(
                cards = listOf(
                    DashboardCard(
                        title = "Attendance",
                        iconRes = Icons.Rounded.FileDownloadDone,
                        colors = listOf(Color(0xFF46A6FF), Color(0xFF4288FD)),
                        extra = null, // Set to null initially, fetch real value later
                        destination = AttendanceRoute.route
                    ),
                    DashboardCard(
                        title = "Assignments",
                        iconRes = Icons.AutoMirrored.Rounded.Assignment,
                        colors = listOf(Color(0xFFFF7043), Color(0xFFF4511E)),
                        badge = null, // Set to null initially, fetch real value later
                        destination = AssignmentsRoute.route
                    )
                ),
                navItems = dashboardRoutes,
                selectedNavIndex = 0
            )
        }

        // Collect user preferences from the DataStore asynchronously
        // and update the UI state when these values change.
        viewModelScope.launch {
            userPreferences.isTeacher.collectLatest { isTeacherValue ->
                Log.d("DashboardViewModel", "Collected isTeacher: $isTeacherValue")
                _uiState.update { it.copy(isTeacher = isTeacherValue) }
                // Trigger data fetch if user role changes or becomes available
                fetchDashboardSummaryData()
            }
        }

        viewModelScope.launch {
            userPreferences.userId.collectLatest { idValue ->
                Log.d("DashboardViewModel", "Collected userId: $idValue")
                _uiState.update { it.copy(id = idValue?.toLongOrNull() ?: 0L) }
                // Trigger data fetch if user ID changes or becomes available
                fetchDashboardSummaryData()
            }
        }

        // Initial fetch of dashboard summary data
        // This will run immediately and also be triggered by the collectLatest blocks
        // when userPreferences provide their initial or updated values.
        fetchDashboardSummaryData()
    }

    /**
     * Fetches summary data for dashboard cards, such as attendance percentage or assignment counts.
     * This function should be called when user role or ID is confirmed.
     */
    private fun fetchDashboardSummaryData() {
        viewModelScope.launch {
            // Retrieve the latest user ID and teacher status from the UI state (updated by collects)
            val currentUserId = _uiState.value.id
            val currentUserIsTeacher = _uiState.value.isTeacher

            if (currentUserId == 0L) {
                Log.w("DashboardViewModel", "User ID is 0, cannot fetch summary data yet.")
                // Optionally update UI state to reflect no data/loading/error
                return@launch
            }

            // --- Placeholder for actual data fetching logic ---
            // You would perform API calls here to get the summary data for each card.
            // Example:
            // val attendanceSummary = if (!currentUserIsTeacher) studentRepository.getOverallAttendance(currentUserId) else teacherRepository.getTeacherClassAttendanceSummary(currentUserId)
            // val assignmentsCount = if (!currentUserIsTeacher) studentRepository.getPendingAssignmentsCount(currentUserId) else teacherRepository.getAssignmentsToGradeCount(currentUserId)
            // --- End Placeholder ---

            // Simulate fetching data (replace with actual repository calls)
            val fetchedAttendancePercent: Double? = 85.0 // Example student attendance
            val fetchedAssignmentsBadge: Int? = 3    // Example assignments count

            // Update the UI state with the fetched data
            _uiState.update { currentState ->
                val updatedCards = currentState.cards.map { card ->
                    when (card.title) {
                        "Attendance" -> card.copy(extra = fetchedAttendancePercent?.toInt()?.toString() + "%")
                        "Assignments" -> card.copy(badge = fetchedAssignmentsBadge)
                        else -> card
                    }
                }
                currentState.copy(cards = updatedCards)
            }
            Log.d("DashboardViewModel", "Dashboard summary data fetched and UI state updated.")
        }
    }


    fun onLogout() {
        viewModelScope.launch {
            tokenManager.clearToken()
            userPreferences.setLoggedIn(false) // Set the login status in preferences
            userPreferences.clearUserInfo() // Also clear userId and isTeacher from preferences

            // Reset the UI state to its initial, default values after logout
            _uiState.update {
                DashboardUiState(
                    cards = listOf( // Re-initialize default cards
                        DashboardCard(
                            title = "Attendance",
                            iconRes = Icons.Rounded.FileDownloadDone,
                            colors = listOf(Color(0xFF46A6FF), Color(0xFF4288FD)),
                            extra = null,
                            destination = AttendanceRoute.route
                        ),
                        DashboardCard(
                            title = "Assignments",
                            iconRes = Icons.AutoMirrored.Rounded.Assignment,
                            colors = listOf(Color(0xFFFF7043), Color(0xFFF4511E)),
                            badge = null,
                            destination = AssignmentsRoute.route
                        )
                    ),
                    navItems = dashboardRoutes,
                    selectedNavIndex = 0,
                    isTeacher = false,
                    id = 0L
                )
            }
            Log.d("DashboardViewModel", "User logged out. UI state reset.")
        }
    }

    fun onNavItemSelected(index: Int) {
        _uiState.update { it.copy(selectedNavIndex = index) }
    }

    // These functions are no longer needed as `isTeacher` and `id` are collected directly
    // from userPreferences and stored in `_uiState`.
    // fun getIsTeacher(): Boolean { return userRepository.isTeacher() ?: false }
    // fun getId(): Long { return userRepository.getUserId() ?: 0L }
}
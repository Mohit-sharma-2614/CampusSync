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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.campussync.data.repository.AuthRepository
import com.example.campussync.persentation.auth.LoginViewModel.LoginEvent
import com.example.campussync.utils.ConnectivityObserver
import com.example.campussync.utils.Resource
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

// --- DashboardCard and DashboardUiState (Minor adjustments) ---
data class DashboardCard(
    val title: String,
    val iconRes: ImageVector,
    val colors: List<Color>,
    val badge: Int? = null,
    val extra: String? = null,
    val destination: String
)

data class DashboardUiState(
    val isLoggedIn: Boolean = true, // Default to false, will be updated by UserPreferences
    val cards: List<DashboardCard> = emptyList(),
    val navItems: List<Routes> = emptyList(),
    val isTeacher: Boolean = false,
    val userId: Long = 0L,
    val selectedNavIndex: Int = 0,
    val isLoading: Boolean = true, // Start as true since we perform initial checks
    val errorMessage: String? = null
)

// --- DashboardViewModel ---
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository,
    private val studentRepository: StudentRepository,
    private val teacherRepository: TeacherRepository,
    connectivityObserver: ConnectivityObserver,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val connectivityStatus: StateFlow<ConnectivityObserver.Status> = connectivityObserver.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConnectivityObserver.Status.Disconnected)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        initializeDashboard()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun initializeDashboard() {
        viewModelScope.launch {
            // Combine user preferences into a single flow for initial state and continuous updates
            combine(
                userPreferences.isLoggedIn,
                userPreferences.isTeacher,
                userPreferences.userId
            ) { isLoggedIn, isTeacher, userId ->
                Triple(isLoggedIn, isTeacher, userId)
            }.onEach { (isLoggedIn, isTeacher, userId) ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoggedIn = isLoggedIn,
                        isTeacher = isTeacher,
                        userId = userId?.toLongOrNull() ?: 0L,
                        isLoading = if (isLoggedIn) currentState.isLoading else false,
                        cards = if (isLoggedIn) currentState.cards else getDefaultDashboardCards()
                    )
                }
                // Only attempt to fetch data if the user is considered logged in and has an ID
                if (isLoggedIn && (userId?.toLongOrNull() ?: 0L) != 0L) {
                    fetchDashboardSummaryData()
                }
            }.launchIn(viewModelScope) // Launch this collector

            // Initial token validation
            val token = tokenManager.getToken()
            Log.d(
                "DashboardViewModel",
                "Initial token validation started with token: |${token ?: "null"}|"
            )
            if (token != null) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) } // Show loading while validating token
                when (isTokenValid(token)) {
                    true -> {
                        Log.d("DashboardViewModel", "Token is valid on startup.")
                        _uiState.update { it.copy(isLoading = false) }
                    }
                    else -> { // "invalid" or "loading" -> treat as invalid for startup check
                        Log.w("DashboardViewModel", "Token invalid or validation failed on startup. Logging out.")
                        _uiState.update { it.copy(
                            isLoading = false,
                            errorMessage = "Session expired. Please log in again."
                        ) }
                        handleTokenInvalidation() // This will call logout()
                    }
                }
            } else {
                // No token found, user is definitively not logged in.
                Log.d("DashboardViewModel", "No token found on startup. Setting isLoggedIn=false.")
                _uiState.update { it.copy(isLoading = false, isLoggedIn = false) }
                // Ensure DataStore reflects this if it somehow got out of sync
                userPreferences.setLoggedIn(false)
            }

            // Set up default cards and nav items regardless of login status
            _uiState.update { currentState ->
                currentState.copy(
                    cards = getDefaultDashboardCards(),
                    navItems = dashboardRoutes,
                    selectedNavIndex = 0
                )
            }
        }
    }

    private suspend fun isTokenValid(token: String): Boolean {
        if (token.isBlank()) {
            Log.w("DashboardViewModel", "Token is blank, considered invalid.")
            return false
        }

        // Server-side validation
        return when (val result = authRepository.validateToken(token)) {
            is Resource.Success -> {
                Log.d("DashboardViewModel", "Online token validation: ${result.data}")
                true
            }
            is Resource.Error -> {
                Log.e("DashboardViewModel", "Server token validation failed: ${result.message}.")
                false
            }
            is Resource.Loading -> {
                Log.d("DashboardViewModel", "Token validation is in loading state.")
                false
            }
        }
    }

    /**
     * Handles token invalidation by logging out the user and resetting UI state.
     */
    private fun handleTokenInvalidation() {
        Log.d("DashboardViewModel", "Token invalid or expired. Clearing user data.")
        viewModelScope.launch {
            logout()
            _uiState.update { it.copy(errorMessage = "Your session has expired. Please log in again.") }
        }
    }

    /**
     * Clears user session data and resets the UI state to a logged-out condition.
     */
    fun logout() {
        viewModelScope.launch {
            tokenManager.clearToken()
            userPreferences.clearUserInfo() // Clears userId, isTeacher, and isLoggedIn in DataStore

            _uiState.update {
                DashboardUiState( // Reset to initial dashboard state for logged out user
                    isLoggedIn = false,
                    isLoading = false,
                    cards = getDefaultDashboardCards(),
                    navItems = dashboardRoutes,
                    selectedNavIndex = 0,
                    isTeacher = false,
                    userId = 0L,
                    errorMessage = null
                )
            }
            Log.d("DashboardViewModel", "User logged out. UI state reset.")
        }
    }

    /**
     * Fetches dynamic dashboard summary data based on user role and updates the UI state.
     */
    private fun fetchDashboardSummaryData() {
        viewModelScope.launch {
            // Avoid redundant fetches if not logged in or user ID is missing
            if (!_uiState.value.isLoggedIn || _uiState.value.userId == 0L) {
                Log.d("DashboardViewModel", "Not logged in or user ID missing, skipping data fetch.")
                _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val currentUserId = _uiState.value.userId
            val currentUserIsTeacher = _uiState.value.isTeacher

            try {
                // Example: Fetching attendance and assignments data
                // Replace with actual API calls to studentRepository/teacherRepository
                val fetchedAttendancePercent: Double?
                val fetchedAssignmentsBadge: Int?

                if (currentUserIsTeacher) {
                    // Example teacher data fetching
                    // val teacherAttendance = teacherRepository.getTeacherAttendanceSummary(currentUserId)
                    // val assignmentsToGrade = teacherRepository.getAssignmentsToGradeCount(currentUserId)
                    fetchedAttendancePercent = 90.0 // Placeholder
                    fetchedAssignmentsBadge = 5 // Placeholder
                } else {
                    // Example student data fetching
                    // val studentAttendance = studentRepository.getOverallAttendance(currentUserId)
                    // val pendingAssignments = studentRepository.getPendingAssignmentsCount(currentUserId)
                    fetchedAttendancePercent = 85.0 // Placeholder
                    fetchedAssignmentsBadge = 3 // Placeholder
                }

                // Update the cards in the UI state
                _uiState.update { currentState ->
                    val updatedCards = currentState.cards.map { card ->
                        when (card.title) {
                            "Attendance" -> card.copy(extra = fetchedAttendancePercent?.toInt()?.toString() + "%")
                            "Assignments" -> card.copy(badge = fetchedAssignmentsBadge)
                            else -> card
                        }
                    }
                    currentState.copy(cards = updatedCards, isLoading = false)
                }
                Log.d("DashboardViewModel", "Dashboard summary data fetched and UI state updated.")

            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error fetching dashboard summary: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load dashboard data. Please try again.") }
            }
        }
    }

    /**
     * Returns a default list of dashboard cards.
     */
    private fun getDefaultDashboardCards(): List<DashboardCard> {
        return listOf(
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
        )
    }

    fun onNavItemSelected(index: Int) {
        _uiState.update { it.copy(selectedNavIndex = index) }
    }
}
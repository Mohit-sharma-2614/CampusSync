package com.example.campussync.persentation.dashboard

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AirplaneTicket
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.example.campussync.data.manager.TokenManager
import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenInputDto
import com.example.campussync.domain.usecases.feature.user.GetUserByIdUseCase
import com.example.campussync.domain.usecases.feature.user.GetUserIdUseCase
import com.example.campussync.domain.usecases.feature.user.LogOutUseCase
import com.example.campussync.navigation.AssignmentsRoute
import com.example.campussync.navigation.AttendanceRoute
import com.example.campussync.persentation.base.AbsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    val cards: List<DashboardCard> = emptyList(),
    val isTeacher: Boolean = false,
    val userId: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DashboardViewModel (
    private val getUserIdUseCase: GetUserIdUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val tokenManager: TokenManager,
    dependencies: AbsDependencies
) : AbsViewModel(dependencies) {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val userId = _uiState.value.userId

    init {
        getUserId()
        buildCards()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun buildCards() {
        _uiState.update {
            it.copy(
                cards = listOf(
                    DashboardCard(
                        title = "Attendance",
                        iconRes = Icons.AutoMirrored.Filled.AirplaneTicket,
                        colors = listOf(
                            Color(0xFF4CAF50),
                            Color(0xFF81C784)
                        ),
                        destination = AttendanceRoute.route
                    ),
                    DashboardCard(
                        title = "Assignments",
                        iconRes = Icons.AutoMirrored.Filled.Notes,
                        colors = listOf(
                            Color(0xFFF44336),
                            Color(0xFFE57373)
                        ),
                        destination = AssignmentsRoute.route
                    )
                )
            )
        }
    }

    fun logOut(){
        viewModelScope.launch {
            val refreshToken = tokenManager.getRefreshToken()
            Log.d("DashboardViewModel", "logOut: $refreshToken")
            if(refreshToken != null){
                val params = LogOutUseCase.Params.forLogOut(RefreshTokenInputDto(refreshToken))
                val authDto = logOutUseCase.execute(params)
                Log.d("DashboardViewModel", "logOut: Logout successful: ${authDto.message}")
                if (authDto.valid) setUnauthenticated()
            }
        }
    }

    fun getUserId(){
        viewModelScope.launch {
            val params = GetUserIdUseCase.Params.forGetUserId()
            val userId = getUserIdUseCase.execute(params)
            _uiState.update { it.copy(userId = userId) }
        }
    }

    fun getUserById(){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val params = GetUserByIdUseCase.Params.forGetUserById(_uiState.value.userId)
            val user = getUserByIdUseCase.execute(params)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

}
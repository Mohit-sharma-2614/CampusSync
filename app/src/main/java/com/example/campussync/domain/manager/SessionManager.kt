package com.example.campussync.domain.manager

import android.util.Log
import com.example.campussync.data.entity.state.SessionState
import com.example.campussync.data.manager.TokenManager
import com.example.campussync.data.manager.UserCredentialManager
import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenInputDto
import com.example.campussync.domain.usecases.feature.user.RefreshTokenUseCase
import com.example.campussync.domain.usecases.feature.user.ValidateTokenUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionManager(
    private val validateTokenUseCase: ValidateTokenUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val tokenManager: TokenManager,
    private val userCredentialManager: UserCredentialManager,
    private val scope: CoroutineScope
) {

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    val sessionState = _sessionState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        scope.launch {
            try {
                val userId = userCredentialManager.getUserId()
                val token = tokenManager.getToken()

                if (userId.isBlank() || token.isBlank()) {
                    _sessionState.value = SessionState.Unauthenticated
                    return@launch
                }

                validateToken(userId, token)

            } catch (e: Exception) {
                Log.e("SessionManager", "Error checking session: ${e.message}")
                _sessionState.value = SessionState.Unauthenticated
            }
        }
    }

    private suspend fun validateToken(userId: String, token: String) {
        try {
            val result = validateTokenUseCase.execute(
                ValidateTokenUseCase.Params.validateToken()
            )

            if (result.valid) {
                _sessionState.value = SessionState.Authenticated
            } else {
                refreshToken(userId, token)
            }

        } catch (e: Exception) {
            Log.e("SessionManager", "Error validating token: ${e.message}")
            _sessionState.value = SessionState.Unauthenticated
        }
    }

    private suspend fun refreshToken(userId: String, token: String) {
        try {
            val result = refreshTokenUseCase.execute(
                RefreshTokenUseCase.Params.forRefreshToken(
                    RefreshTokenInputDto(
                        userId.toLong(),
                        token,
                        "no-info"
                    )
                )
            )

            tokenManager.saveToken(result.accessToken)
            tokenManager.saveRefreshToken(result.refreshToken)

            _sessionState.value = SessionState.Authenticated

        } catch (e: Exception) {
            Log.e("SessionManager", "Error refreshing token: ${e.message}")
            logout()
        }
    }

    fun logout() {
        scope.launch {
            tokenManager.clearToken()
            tokenManager.clearRefreshToken()
            userCredentialManager.clearUserId()
            _sessionState.value = SessionState.Unauthenticated
        }
    }
}
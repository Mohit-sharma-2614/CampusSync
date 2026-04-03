package com.example.campussync.domain.manager

import android.util.Log
import com.example.campussync.data.entity.state.SessionState
import com.example.campussync.data.manager.TokenManager
import com.example.campussync.data.manager.UserCredentialManager
import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenInputDto
import com.example.campussync.domain.usecases.feature.user.GetUserByIdUseCase
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
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val scope: CoroutineScope
) {
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    val sessionState = _sessionState.asStateFlow()

    init {
        checkSession()
    }

    // Changed to suspend to avoid runBlocking
    private suspend fun getUserId(): String {
        val userId = getUserByIdUseCase.execute(GetUserByIdUseCase.Params.forGetUserId())
        Log.d("SessionManager", "getUserId: $userId")
        return userId
    }

    private fun checkSession() {
        scope.launch {
            try {
                val userId = getUserId()
                val token = tokenManager.getToken()
                Log.i("SessionManager", "checkSession: $userId $token")

                if (userId.isBlank() || token.isBlank()) {
                    _sessionState.value = SessionState.Unauthenticated
                    Log.e("SessionManager", "checkSession session state: Unauthenticated")
                    return@launch
                }
                validateToken(userId, token)
            } catch (e: Exception) {
                Log.e("SessionManager", "checkSession: ${e.message}")
                _sessionState.value = SessionState.Unauthenticated
                Log.e("SessionManager", "session state: Unauthenticated")
            }
        }
    }

    private suspend fun validateToken(userId: String, token: String) {
        Log.d("SessionManager", "validateToken: entered $userId $token")
        try {
            val result = validateTokenUseCase.execute(ValidateTokenUseCase.Params.validateToken())
            Log.i("SessionManager", "validateToken: $result")
            if (result.valid) {
                _sessionState.value = SessionState.Authenticated
                Log.e("SessionManager", "validateToken session state: Authenticated")
            } else {
                refreshToken(userId, token)
            }
        } catch (e: Exception) {
            Log.e("SessionManager", "validateToken: ${e.message}")
            refreshToken(userId, token) // Try refresh on network error/failure
        }
    }

    private suspend fun refreshToken(userId: String, token: String) {
        try {
            val result = refreshTokenUseCase.execute(
                RefreshTokenUseCase.Params.forRefreshToken(RefreshTokenInputDto(token))
            )
            Log.i("SessionManager", "refreshToken: $result")
            tokenManager.saveToken(result.accessToken)
            tokenManager.saveRefreshToken(result.refreshToken)
            _sessionState.value = SessionState.Authenticated
            Log.e("SessionManager", "refreshToken session state: Authenticated")
        } catch (e: Exception) {
            Log.e("SessionManager", "refreshToken: ${e.message}")
            logout()
        }
    }

    fun logout() {
        scope.launch {
            tokenManager.clearToken()
            tokenManager.clearRefreshToken()
            userCredentialManager.clearUserId()
            _sessionState.value = SessionState.Unauthenticated
            Log.e("SessionManager", "logout: session state: Unauthenticated")
        }
    }
}
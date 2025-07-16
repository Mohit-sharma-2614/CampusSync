package com.example.campussync.persentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.jwt.JWT
import com.example.campussync.data.model.student.StudentLoginRequest
import com.example.campussync.data.model.student.StudentLoginResponse
import com.example.campussync.data.model.teacher.TeacherLoginRequest
import com.example.campussync.data.model.teacher.TeacherLoginResponse
import com.example.campussync.data.repository.AuthRepository
import com.example.campussync.data.repository.StudentRepository
import com.example.campussync.data.repository.TeacherRepository
import com.example.campussync.utils.ConnectivityObserver
import com.example.campussync.utils.Resource
import com.example.campussync.utils.TokenManager
import com.example.campussync.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val email: String = "",
    val password: String = "",
    val isTeacherLoginAttempt: Boolean = false, // Renamed for clarity: reflects the role chosen for login attempt
    val errorMessage: String? = null,
    val token: String? = null,
    val student: StudentLoginResponse? = null,
    val teacher: TeacherLoginResponse? = null,
    val currentUserId: String? = null, // From UserPreferences
    val currentUserIsTeacher: Boolean = false // From UserPreferences
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val teacherRepository: TeacherRepository,
    private val studentRepository: StudentRepository,
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences,
    private val connectivityObserver: ConnectivityObserver,
    private val tokenManager: TokenManager
) : ViewModel() {

    val connectivityStatus: StateFlow<ConnectivityObserver.Status> = connectivityObserver.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConnectivityObserver.Status.Disconnected)

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

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
    fun logForDebug(){
        Log.d("LoginViewModel","UserPrefs: ${userId.value} ${isTeacher.value} ${isLoggedIn.value}")
    }

    private val _loginEvents = Channel<LoginEvent>(Channel.BUFFERED)
    val loginEvents: Flow<LoginEvent> = _loginEvents.receiveAsFlow()

    // Sealed class for login events
    sealed class LoginEvent {
        data class Error(val message: String) : LoginEvent()
        object Success : LoginEvent()
        object TokenExpired : LoginEvent() // Added for explicit token expiration
    }

    init {

        viewModelScope.launch {

            userPreferences.isLoggedIn.collect { loggedIn ->
                _loginState.update { it.copy(isLoggedIn = loggedIn) }
            }

            launch {
                userPreferences.userId.collect { id ->
                    _loginState.update { it.copy(currentUserId = id) }
                }
            }
            launch {
                userPreferences.isTeacher.collect { teacherStatus ->
                    _loginState.update { it.copy(currentUserIsTeacher = teacherStatus) }
                }
            }
        }

//        viewModelScope.launch {
//            logForDebug()
//            val token = tokenManager.getToken()
//            val initialLoggedIn = userPreferences.isLoggedIn.first()
//            val initialUserId = userPreferences.userId.first()
//            val initialIsTeacher = userPreferences.isTeacher.first()
//            _loginState.update {
//                it.copy(
//                    currentUserId = initialUserId,
//                    currentUserIsTeacher = initialIsTeacher,
//                    isLoggedIn = initialLoggedIn
//                )
//            }
//
//            if (token != null) {
//                val validationResult = isTokenValid(token)
//            when (validationResult) {
//                "valid" -> {
//                    _loginState.update {
//                        it.copy(
//                            isLoggedIn = true,
//                            token = token,
//                            isLoading = false
//                        )
//                    }
//                }
//                else -> handleTokenInvalidation()
//            }
//        } else {
//            _loginState.update { it.copy(isLoading = false, isLoggedIn = false) }
//        }
//
//        }
    }

    /**
     * Call this function after a successful login API call to persist user info.
     * @param isTeacherStatus The actual role of the logged-in user.
     * @param id The ID of the logged-in user.
     */
    fun onLoginSuccess(isTeacherStatus: Boolean, id: Long) {
        viewModelScope.launch {
            try {
                userPreferences.setUserInfo(id.toString(), isTeacherStatus)
                userPreferences.setLoggedIn(true)
                _loginEvents.send(LoginEvent.Success)
                _loginState.update { it.copy(isLoading = false, errorMessage = null, isLoggedIn = true) }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Failed to persist login state: ${e.message}")
                _loginEvents.send(LoginEvent.Error("Failed to save login state."))
                _loginState.update { it.copy(isLoading = false, errorMessage = "Failed to save login state.") }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true, errorMessage = null) }
            val isTeacherLoginAttempt = _loginState.value.isTeacherLoginAttempt // Use the attempt flag

            val result = if (isTeacherLoginAttempt) {
                loginTeacher(email, password)
            } else {
                loginStudent(email, password)
            }

            _loginState.update { currentState ->
                when (result) {
                    is Resource.Success -> {
                        val data = result.data
                        Log.d("login", data.toString())
                        val newToken = if (isTeacherLoginAttempt) (data as? TeacherLoginResponse)?.token else (data as? StudentLoginResponse)?.token
                        val userId = if (isTeacherLoginAttempt) (data as? TeacherLoginResponse)?.id else (data as? StudentLoginResponse)?.id
                        val actualIsTeacherStatus = isTeacherLoginAttempt // Assuming the login attempt role is the actual role

                        if (newToken != null && userId != null) {
                            Log.d("LoginViewModel", "Login successful. Saving token: ${newToken.substring(0, 10)}... (truncated)")
                            Log.d("LoginViewModel", "RAW Token from API: |${newToken}|")
                            Log.d("LoginViewModel", "Length of RAW Token: ${newToken.length}")
                            val trimmedToken = newToken.trim() // Make sure you are trimming before saving
                            Log.d("LoginViewModel", "Trimmed Token for saving: |${trimmedToken}|")
                            Log.d("LoginViewModel", "Length of TRIMMED Token: ${trimmedToken.length}")
                            tokenManager.saveToken(trimmedToken)
                            onLoginSuccess(actualIsTeacherStatus, userId) // Persist user info
                        } else {
                            // Handle cases where token or ID might be missing from response
                            Log.e("LoginViewModel", "Login successful but token or ID missing from response for ${if (isTeacherLoginAttempt) "Teacher" else "Student"}.")
                            _loginEvents.send(LoginEvent.Error("Login successful but user data incomplete."))
                            return@update currentState.copy(
                                isLoading = false,
                                errorMessage = "Login successful but user data incomplete."
                            )
                        }

                        currentState.copy(
                            isLoggedIn = true,
                            token = newToken,
                            teacher = if (isTeacherLoginAttempt) (data as? TeacherLoginResponse) else null,
                            student = if (!isTeacherLoginAttempt) (data as? StudentLoginResponse) else null,
                            errorMessage = null,
                            isLoading = false
                        )
                    }
                    is Resource.Error -> {
                        Log.e("LoginViewModel", "Login failed: ${result.message}")
                        _loginEvents.send(LoginEvent.Error(result.message ?: "An unknown error occurred"))
                        currentState.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                    is Resource.Loading -> currentState.copy(isLoading = true) // Should ideally not happen here
                }
            }
        }
    }

    fun onLoginClick() {
        val email = _loginState.value.email
        val password = _loginState.value.password
        if (email.isBlank() || password.isBlank()) {
            _loginState.update { it.copy(errorMessage = "Email and password cannot be empty.") }
            viewModelScope.launch { _loginEvents.send(LoginEvent.Error("Email and password cannot be empty.")) }
            return
        }
        login(email, password)
    }

    private suspend fun isTokenValid(token: String): Boolean {
        if (token.isBlank()) {
            Log.w("TokenValidation", "Token is blank, considered invalid.")
            return false
        }

        // Server-side validation
        val result = authRepository.validateToken(token)
        return when (result) {
            is Resource.Success -> {
                Log.d("token", "online token validation: ${result.data}")
                result.data.valid
            }
            is Resource.Error -> {
                Log.d("token", "Server token validation failed: ${result.message}.")
                false
            }
            is Resource.Loading -> {
                Log.d("TokenValidation", "Token validation is in loading state.")
                false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearUserInfo() // Clears userId, isTeacher, and isLoggedIn
            tokenManager.clearToken()
            _loginState.update {
                it.copy(
                    isLoggedIn = false,
                    token = null,
                    student = null,
                    teacher = null,
                    errorMessage = null,
                    currentUserId = null,
                    currentUserIsTeacher = false
                )
            }
            _loginEvents.send(LoginEvent.Success) // Or a specific LogoutEvent if desired
        }
    }

    private fun handleTokenInvalidation() {
        Log.d("LoginViewModel", "Token invalid or expired. Clearing user data.")
        logout()
    }

    fun updateErrorMessage() {
        _loginState.update { it.copy(errorMessage = null) }
    }

    fun updateEmail(email: String) {
        _loginState.update { it.copy(email = email) }
    }

    fun updatePassword(password: String) {
        _loginState.update { it.copy(password = password) }
    }

    fun updateLoginAttemptRole(isTeacher: Boolean) { // Renamed for clarity
        _loginState.update { it.copy(isTeacherLoginAttempt = isTeacher) }
    }

    private suspend fun loginTeacher(email: String, password: String): Resource<TeacherLoginResponse> {
        val teacher = TeacherLoginRequest(email, password)
        return teacherRepository.loginTeacher(teacher)
    }

    private suspend fun loginStudent(email: String, password: String): Resource<StudentLoginResponse> {
        val student = StudentLoginRequest(email, password)
        return studentRepository.loginStudent(student)
    }

    fun retryLogin() {
        val email = _loginState.value.email
        val password = _loginState.value.password
        if (email.isBlank() || password.isBlank()) {
            _loginState.update { it.copy(errorMessage = "Email and password cannot be empty for retry.") }
            viewModelScope.launch { _loginEvents.send(LoginEvent.Error("Email and password cannot be empty for retry.")) }
            return
        }
        login(email, password)
    }

}
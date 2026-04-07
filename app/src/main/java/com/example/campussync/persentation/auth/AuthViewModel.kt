package com.example.campussync.persentation.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.campussync.data.entity.state.UiState
import com.example.campussync.data.remote.dto.enums.UserRole
import com.example.campussync.data.remote.dto.user.UserDto
import com.example.campussync.data.remote.dto.user.UserLoginDto
import com.example.campussync.domain.usecases.feature.user.LoginUseCase
import com.example.campussync.domain.usecases.feature.user.isCorrectEmail
import com.example.campussync.persentation.base.AbsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.ExperimentalTime

data class LoginCreds(
    val email: String,
    val password: String,
    val isTeacherLoginAttempt: Boolean
)

data class CredsError(
    val emailError: String?,
    val passwordError: String?
)

data class UiData @OptIn(ExperimentalTime::class) constructor(
    val userId: Long = 0,
    val loginCreds: LoginCreds = LoginCreds("", "", false),
    val credsError: CredsError = CredsError(null,null),
    val userDtoData: UserDto = UserDto(null, name = "--", email = "--", null, null,null),
    val errorMessage: String? = null,
)

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    dependencies: AbsDependencies,
) : AbsViewModel(dependencies) {

    private val _uiData = MutableStateFlow(UiData())
    val uiData: StateFlow<UiData> = _uiData.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiData.update { it.copy(loginCreds = it.loginCreds.copy(email = email.trim())) }
    }

    fun onPasswordChange(password: String) {
        _uiData.update { it.copy(loginCreds = it.loginCreds.copy(password = password.trim())) }
    }

    fun toggleLoginType(isTeacher: Boolean) {
        _uiData.update { it.copy(loginCreds = it.loginCreds.copy(isTeacherLoginAttempt = isTeacher)) }
    }

    fun resetCredError() {
        _uiData.update {
            it.copy(
                credsError = it.credsError.copy(
                    emailError = null,
                    passwordError = null
                )
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    fun login() {
        resetCredError()
        val creds = _uiData.value.loginCreds
        val email = creds.email
        val password = creds.password
        var hasError = false

        if (!email.isCorrectEmail()) {
            _uiData.update { it.copy(credsError = it.credsError.copy(emailError = "Invalid email format")) }
            hasError = true
        }

        if (password.length < 6) {
            _uiData.update { it.copy(credsError = it.credsError.copy(passwordError = "Password too short")) }
            hasError = true
        }

        if (hasError) return

        _uiState.value = UiState.Loading

        val dto = UserLoginDto(email, password)
        val params = if (creds.isTeacherLoginAttempt) {
            Log.d("AuthViewModel", "login: preparing teacher login")
            LoginUseCase.Params.teacher(dto)
        } else {
            Log.d("AuthViewModel", "login: preparing student login")
            LoginUseCase.Params.student(dto)
        }

        Log.d("AuthViewModel", "login: calling loginUseCase.execute")
        loginUseCase.execute(
            params,
            viewModelScope,
            onSuccess = { user ->
                Log.d("AuthViewModel", "login: onSuccess triggered")
                if (creds.isTeacherLoginAttempt) {
                    val teacher = user as com.example.campussync.domain.model.User.Teacher
                    _uiData.update {
                        it.copy(
                            userDtoData = it.userDtoData.copy(
                                id = teacher.id,
                                name = teacher.name,
                                email = teacher.email,
                                role = UserRole.TEACHER,
                                status = null,
                                createdAt = null,
                            )
                        )
                    }
                } else {
                    val student = user as com.example.campussync.domain.model.User.Student
                    _uiData.update {
                        it.copy(
                            userDtoData = it.userDtoData.copy(
                                id = student.id,
                                name = student.name,
                                email = student.email,
                                role = UserRole.STUDENT,
                                status = null,
                                createdAt = null,
                            )
                        )
                    }
                }
                _uiState.value = UiState.Success(user)
                setAuthenticated()
            },
            onError = { throwable ->
                Log.e("AuthViewModel", "login: onError triggered - ${throwable.message}")
                _uiState.value = UiState.Error(handleError(throwable, "Login Failed").message)
                _uiData.update { it.copy(errorMessage = throwable.message) }
            }
        )
    }
}

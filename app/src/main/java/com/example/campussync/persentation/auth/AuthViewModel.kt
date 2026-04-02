package com.example.campussync.persentation.auth

import androidx.lifecycle.viewModelScope
import com.example.campussync.data.entity.state.UiState
import com.example.campussync.data.remote.dto.enums.UserRole
import com.example.campussync.data.remote.dto.user.User
import com.example.campussync.data.remote.dto.user.UserLoginDto
import com.example.campussync.domain.usecases.feature.user.LoginUseCase
import com.example.campussync.domain.usecases.feature.user.isCorrectEmail
import com.example.campussync.domain.usecases.feature.user.isCorrectPassword
import com.example.campussync.persentation.base.AbsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginCreds(
    val email: String,
    val password: String,
    val isTeacherLoginAttempt: Boolean
)

data class CredsError(
    val emailError: String?,
    val passwordError: String?
)

data class UiData(
    val userId: Long = 0,
    val loginCreds: LoginCreds = LoginCreds("", "", false),
    val credsError: CredsError = CredsError(null,null),
    val userData: User = User(null, name = "--", email = "--", null, null,null),
    val errorMessage: String? = null,
)

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    dependencies: AbsDependencies,
) : AbsViewModel(dependencies) {

    private val _uiData = MutableStateFlow(UiData())
    val uiData: StateFlow<UiData> = _uiData.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle) // Start with Idle
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

    fun login() {
        val creds = _uiData.value.loginCreds
        val email = creds.email
        val password = creds.password
        if(!email.isCorrectEmail()){
            _uiData.update { it.copy(
                credsError = it.credsError.copy(emailError = "Email should be valid. :)"))
            }
            return
        }
        if (!password.isCorrectPassword()){
            _uiData.update { it.copy(
                credsError = it.credsError.copy(passwordError = "Password must be 8 words: T_T")
            ) }
            return
        }

        _uiState.value = UiState.Loading

        val dto = UserLoginDto(email, password)
        val params = if (creds.isTeacherLoginAttempt) {
            LoginUseCase.Params.teacher(dto)
        } else {
            LoginUseCase.Params.student(dto)
        }

        loginUseCase.execute(
            params,
            viewModelScope,
            onSuccess = { user ->
                if(creds.isTeacherLoginAttempt){
                    val teacher = user as com.example.campussync.domain.model.User.Teacher
                    _uiData.update { it.copy(
                        userData = it.userData.copy(
                            id = teacher.id,
                            name = teacher.name,
                            email = teacher.email,
                            role = UserRole.STUDENT,
                            status = null,
                            createdAt = null,
                        )
                    ) }
                } else {
                    val student = user as com.example.campussync.domain.model.User.Student
                    _uiData.update { it.copy(
                        userData = it.userData.copy(
                            id = student.id,
                            name = student.name,
                            email = student.email,
                            role = UserRole.STUDENT,
                            status = null,
                            createdAt = null,
                        )
                    ) }
                }
                // Note: Ensure your LoginUseCase saves the token to TokenManager internally
                // Or call sessionManager.triggerManualCheck() here
                _uiState.value = UiState.Success(user)
            },
            onError = { throwable ->
                _uiState.value = UiState.Error(handleError(throwable, "Login Failed").message)
            }
        )
    }
}
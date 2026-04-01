package com.example.campussync.persentation.auth

import androidx.lifecycle.viewModelScope
import com.example.campussync.data.entity.state.UiState
import com.example.campussync.data.remote.dto.user.UserLoginDto
import com.example.campussync.domain.model.User
import com.example.campussync.domain.usecases.feature.user.LoginUseCase
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

data class UiData(
    val userId: Long = 0,
    val loginCreds: LoginCreds = LoginCreds("", "", false),
    val isTeacherLoginAttempt: Boolean = false,
    val errorMessage: String? = null,
)

class AuthViewModel (
    private val loginUseCase: LoginUseCase,
    dependencies: AbsDependencies,
) : AbsViewModel(dependencies) {

    private val _uiData = MutableStateFlow(UiData())
    val uiData: StateFlow<UiData> = _uiData.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    fun onEmailChange(email: String) {
        _uiData.update {
            it.copy(loginCreds = it.loginCreds.copy(email = email))
        }
    }
    fun onPasswordChange(password: String) {
        _uiData.update {
            it.copy(loginCreds = it.loginCreds.copy(password = password))
        }
    }

    fun login(){
        _uiState.update {
            UiState.Loading
        }
        val loginCreds = _uiData.value.loginCreds

        val isTeacherLoginAttempt = loginCreds.isTeacherLoginAttempt
        val logiUserDto = UserLoginDto(
            email = loginCreds.email,
            password = loginCreds.password
        )

        if (isTeacherLoginAttempt){
            val params = LoginUseCase.Params.teacher(logiUserDto)

            loginUseCase.execute(
                params,
                viewModelScope,
                onSuccess = {
                    val user = it as User.Teacher
                    _uiState.update {
                        UiState.Success(user)
                    }
                },
                onError = {
                    _uiState.update {
                        UiState.Error("Something went wrong")
                    }
                }
            )
        } else {
            val params = LoginUseCase.Params.student(logiUserDto)

            loginUseCase.execute(
                params,
                viewModelScope,
                onSuccess = {
                    val user = it as User.Student
                    _uiState.update {
                        UiState.Success(user)
                    }
                },
                onError = {
                    _uiState.update {
                        UiState.Error("Something went wrong")
                    }
                }
            )
        }
    }


}
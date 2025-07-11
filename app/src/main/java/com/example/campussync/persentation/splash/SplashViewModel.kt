package com.example.campussync.persentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campussync.navigation.Dashboard
import com.example.campussync.navigation.Login
import com.example.campussync.persentation.auth.LoginViewModel
import com.example.campussync.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashState(
    val isLoading: Boolean = true,
    val navigateTo: String? = null
)

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _splashState = MutableStateFlow(SplashState())
    val splashState: StateFlow<SplashState> = _splashState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferences.isLoggedIn.collect { isLoggedIn ->
                _splashState.value = SplashState(
                    isLoading = false,
                    navigateTo = if (isLoggedIn) Dashboard.route else Login.route
                )
            }
        }
    }
}

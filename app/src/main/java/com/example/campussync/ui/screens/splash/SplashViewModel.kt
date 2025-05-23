package com.example.campussync.ui.screens.splash

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SplashUiEvent {
    data object NavigateToLogin : SplashUiEvent()
    data object NavigateToDashboard : SplashUiEvent()
}

class SplashViewModel() : ViewModel() {

    private val _event = MutableStateFlow<SplashUiEvent?>(null)
    val event: StateFlow<SplashUiEvent?> = _event

    init {
        viewModelScope.launch {
            delay(2000) // Simulate loading time
            // Mock login check
            val isLoggedIn = false // ← you can flip to true to test
            _event.value = if (isLoggedIn) {
                SplashUiEvent.NavigateToDashboard
            } else {
                SplashUiEvent.NavigateToLogin
            }
        }
    }

    fun clearEvent() {
        _event.value = null
    }
}
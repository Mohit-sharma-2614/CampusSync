package com.example.campussync.data.entity.state

sealed class SessionState {
    object Loading : SessionState()
    object Authenticated : SessionState()
    object Unauthenticated : SessionState()
}
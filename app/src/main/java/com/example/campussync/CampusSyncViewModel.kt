package com.example.campussync

import com.example.campussync.data.entity.state.SessionState
import com.example.campussync.persentation.base.AbsViewModel

class CampusSyncViewModel(
    dependencies: AbsDependencies
) : AbsViewModel(dependencies) {
    private val _sessionState = appState.value.session

    init {
        checkAppState()
    }

    fun checkAppState(){
        when(_sessionState){
            SessionState.Loading -> {
                // Show loading
            }

            SessionState.Authenticated -> {
                // Go to Dashboard
            }

            SessionState.Unauthenticated -> {
                // Go  to login screen
            }
        }
    }
}
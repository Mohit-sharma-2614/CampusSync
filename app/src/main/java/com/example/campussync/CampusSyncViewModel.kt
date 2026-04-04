package com.example.campussync

import com.example.campussync.data.observer.NetworkObserver
import com.example.campussync.persentation.base.AbsViewModel

class CampusSyncViewModel(
    private val observeNetworkUseCase: NetworkObserver,
    dependencies: AbsDependencies
) : AbsViewModel(dependencies) {
    private val _sessionState = appState.value.session

    fun onRetryClick(){
        observeNetworkUseCase.observe()
    }
}
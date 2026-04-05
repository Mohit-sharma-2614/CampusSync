package com.example.campussync

import com.example.campussync.data.observer.NetworkObserver
import com.example.campussync.persentation.base.AbsViewModel
import com.example.campussync.utils.config.TopBarConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CampusSyncViewModel(
    private val observeNetworkUseCase: NetworkObserver,
    dependencies: AbsDependencies
) : AbsViewModel(dependencies) {
    private val _sessionState = appState.value.session

    private val _topBarConfig =
        MutableStateFlow(TopBarConfig(title = "CampusSync"))
    val topBarConfig: StateFlow<TopBarConfig> = _topBarConfig.asStateFlow()

    fun setTopBar(config: TopBarConfig){
        _topBarConfig.value = config
    }


    fun onRetryClick(){
        observeNetworkUseCase.observe()
    }
}
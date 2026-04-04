package com.example.campussync.persentation.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campussync.data.entity.common.ErrorEntity
import com.example.campussync.data.entity.state.SessionState
import com.example.campussync.data.observer.NetworkObserver
import com.example.campussync.domain.manager.SessionManager
import com.example.campussync.domain.usecases.feature.network.CheckNetworkUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn

data class AppState(
    val session: SessionState,
    val network: NetworkObserver.Status
)

abstract class AbsViewModel (
    private val dependencies: AbsDependencies
): ViewModel() {

    val networkStatus: StateFlow<NetworkObserver.Status> =
        dependencies.checkNetworkUseCase()
            .distinctUntilChanged()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                NetworkObserver.Status.Available
            )
    val sessionState = dependencies.sessionManager.sessionState

    val appState: StateFlow<AppState> =
        combine(sessionState, networkStatus) { session, network ->
            AppState(session, network)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AppState(SessionState.Loading, NetworkObserver.Status.Available)
        )

    fun setAuthenticated() {
        dependencies.sessionManager.setAuthenticated()
    }

    fun setUnauthenticated() {
        dependencies.sessionManager.setUnauthenticated()
    }

    protected fun handleError(throwable: Throwable, error: String = ""): ErrorEntity {
        Log.e("AbsViewModel","$error: "+throwable.message.toString())
        return ErrorEntity(throwable.message ?: "")
    }

    class AbsDependencies(
        val checkNetworkUseCase: CheckNetworkUseCase,
        val sessionManager: SessionManager
    )
}
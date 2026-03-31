package com.example.campussync.di

import com.example.campussync.utils.ConnectivityObserver
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import org.koin.dsl.module

val connectivityObserverModule = module {
    single<StateFlow<ConnectivityObserver.Status>> {
        get<ConnectivityObserver>()
            .observe()
            .distinctUntilChanged()
            .stateIn(
                get(), // shared scope
                SharingStarted.Eagerly,
                ConnectivityObserver.Status.Connected
            )
    }
}
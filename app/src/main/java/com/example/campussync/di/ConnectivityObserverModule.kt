package com.example.campussync.di

import com.example.campussync.data.observer.NetworkObserver
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import org.koin.dsl.module

val connectivityObserverModule = module {
    single<StateFlow<NetworkObserver.Status>> {
        get<NetworkObserver>()
            .observe()
            .distinctUntilChanged()
            .stateIn(
                get(), // shared scope
                SharingStarted.Eagerly,
                NetworkObserver.Status.Available
            )
    }
}
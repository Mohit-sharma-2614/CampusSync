package com.example.campussync.domain.usecases.feature.network

import com.example.campussync.data.observer.NetworkObserver
import kotlinx.coroutines.flow.Flow

class CheckNetworkUseCase(
    private val networkObserver: NetworkObserver
) {
    operator fun invoke(): Flow<NetworkObserver.Status> {
        return networkObserver.observe()
    }
}
package com.example.campussync.domain.observer

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.campussync.data.observer.NetworkObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class NetworkObserverImpl(
    private val context: Context
) : NetworkObserver{
    override fun observe(): Flow<NetworkObserver.Status> = callbackFlow {

        val connectivityManager by lazy {
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }

        val networkCallback = object : ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(NetworkObserver.Status.Available)
            }
            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(NetworkObserver.Status.Lost)
            }
            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                trySend(NetworkObserver.Status.Losing)
            }
            override fun onUnavailable() {
                super.onUnavailable()
                trySend(NetworkObserver.Status.Unavailable)
            }
        }

        val request: NetworkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, networkCallback)

        // Send initial state
        val isConnected = connectivityManager.activeNetwork?.let { network ->
            connectivityManager.getNetworkCapabilities(network)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } ?: false
        trySend(if (isConnected) NetworkObserver.Status.Available else NetworkObserver.Status.Unavailable)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }

    }.distinctUntilChanged()
}
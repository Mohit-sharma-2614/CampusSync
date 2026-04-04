package com.example.campussync.domain.observer

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.example.campussync.data.observer.NetworkObserver
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

class NetworkObserverImpl(
    private val context: Context
) : NetworkObserver{
    @OptIn(FlowPreview::class)
    override fun observe(): Flow<NetworkObserver.Status> = callbackFlow {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val caps = connectivityManager.getNetworkCapabilities(network)
                val hasInternet = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
                trySend(
                    if(hasInternet) NetworkObserver.Status.Available
                    else NetworkObserver.Status.Unavailable
                )
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

        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        // Initial state (IMPORTANT FIX)
        val activeNetwork = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(activeNetwork)

        val isConnected = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true

        trySend(
            if (isConnected) NetworkObserver.Status.Available
            else NetworkObserver.Status.Unavailable
        )

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }

//        val request: NetworkRequest = NetworkRequest.Builder()
//            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
//            .build()
//
//        connectivityManager.registerNetworkCallback(request, networkCallback)
//
//        // Send initial state
//        val isConnected = connectivityManager.activeNetwork?.let { network ->
//            connectivityManager.getNetworkCapabilities(network)
//                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
//        } ?: false
//        trySend(if (isConnected) NetworkObserver.Status.Available else NetworkObserver.Status.Unavailable)
//
//        awaitClose {
//            connectivityManager.unregisterNetworkCallback(networkCallback)
//        }

    }
        .debounce(300)
        .distinctUntilChanged()
}
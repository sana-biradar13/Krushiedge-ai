package com.krushiedge.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.krushiedge.domain.model.ConnectivityStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Network Connectivity Monitor.
 * Monitors real-time internet availability, cellular signal quality,
 * and emits status changes for offline-first UI adaptation.
 */
@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _status = MutableStateFlow(checkInitialStatus())
    val status = _status.asStateFlow()

    init {
        registerNetworkCallback()
    }

    private fun checkInitialStatus(): ConnectivityStatus {
        val activeNetwork = connectivityManager.activeNetwork ?: return ConnectivityStatus.OFFLINE
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return ConnectivityStatus.OFFLINE
        return if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            ConnectivityStatus.ONLINE
        } else {
            ConnectivityStatus.OFFLINE
        }
    }

    private fun registerNetworkCallback() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _status.value = ConnectivityStatus.ONLINE
            }

            override fun onLost(network: Network) {
                _status.value = ConnectivityStatus.OFFLINE
            }

            override fun onUnavailable() {
                _status.value = ConnectivityStatus.OFFLINE
            }
        })
    }

    fun isOnline(): Boolean = _status.value == ConnectivityStatus.ONLINE

    fun observeConnectivity(): Flow<ConnectivityStatus> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(ConnectivityStatus.ONLINE)
            }

            override fun onLost(network: Network) {
                trySend(ConnectivityStatus.OFFLINE)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)
        trySend(checkInitialStatus())

        awaitClose {
            try {
                connectivityManager.unregisterNetworkCallback(callback)
            } catch (e: Exception) {
                // Ignore if unregister fails
            }
        }
    }
}

package com.hopeinyang.zeroknowledge.data.dao.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.net.NetworkRequest
import com.hopeinyang.zeroknowledge.data.dao.ConnectivityObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class ConnectivityObserverImpl @Inject constructor(
    private val context: Context
):ConnectivityObserver {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override val hasNet: Boolean
        get() =
            try {
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                (capabilities?.hasCapability(NET_CAPABILITY_VALIDATED)) ?: false
            } catch (exception: SecurityException){
                false
            }

    override fun observer(): Flow<ConnectivityObserver.InternetStatus> = callbackFlow {


        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                trySend(ConnectivityObserver.InternetStatus.Available)

            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                trySend(ConnectivityObserver.InternetStatus.Losing)

            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(ConnectivityObserver.InternetStatus.Lost)

            }

            override fun onUnavailable() {
                super.onUnavailable()
                trySend(ConnectivityObserver.InternetStatus.Unavailable)

            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val internetAvailable = networkCapabilities.hasCapability(NET_CAPABILITY_VALIDATED)
                //Log.d("Network Cap", internetAvailable.toString())
                if (internetAvailable) {
                    trySend(ConnectivityObserver.InternetStatus.Available)
                    //launch { send(ConnectivityObserver.InternetStatus.Available) }

                } else {
                    trySend(ConnectivityObserver.InternetStatus.InternetLost)
                    //launch { send(ConnectivityObserver.InternetStatus.InternetLost) }
                }

            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
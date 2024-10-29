package com.hopeinyang.zeroknowledge.data.dao

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {

    fun observer(): Flow<InternetStatus>

    //fun observeConnectivityAsFlow():Flow<ConnectionState>
    val  hasNet: Boolean
    //fun checkNetwork() : Boolean

    enum class InternetStatus{
        Available, Unavailable, Losing, Lost, InternetLost
    }
}
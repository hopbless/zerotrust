package com.hopeinyang.zeroknowledge.data.dao

import android.content.Context
import android.location.Location
import androidx.activity.result.IntentSenderRequest


import com.hopeinyang.zeroknowledge.data.dto.Response
import kotlinx.coroutines.flow.Flow

interface LocationService {

    fun getUserCurrentLocation(): Flow<Response<Location?>>

    fun enabledLocationRequest(context: Context,)

    fun isConnected():Boolean
}
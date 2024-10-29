package com.hopeinyang.zeroknowledge.data.dao.impl

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.type.LatLng
import com.hopeinyang.zeroknowledge.data.dao.LocationService
import com.hopeinyang.zeroknowledge.data.dto.Response
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy

import timber.log.Timber
import javax.inject.Inject

class LocationServiceImpl @Inject constructor (
    private val context: Context
) {

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
      fun getUserCurrentLocation(): Flow<Response<Location?>> = callbackFlow {


            trySend(Response.loading(null))

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,

            object : CancellationToken(){
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token


                override fun isCancellationRequested(): Boolean = false


            }).addOnSuccessListener { location ->
                if (location != null) {
                    trySend(Response.success(location))
                    Timber.e("Location is Success with ${location.latitude} and ${location.longitude}")

                }else{
                    Timber.e("Location is null")
                    trySend(Response.error("could not fetch location", null))
                }

        }
            .addOnFailureListener {
                trySend(Response.error("could not fetch location", null))
                Timber.e("Failed to retrieve location with error: ${it.message.toString()}")
            }
            .addOnCanceledListener {
                trySend(Response.error("could not fetch location", null))
                Timber.e("location request cancelled")
            }
        awaitClose {  }
    }

     fun enabledLocationRequest(context: Context,) {
        TODO("Not yet implemented")
    }

     fun isConnected(): Boolean {
        TODO("Not yet implemented")
    }
}
package com.hopeinyang.zeroknowledge.data.dao.impl

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.hopeinyang.zeroknowledge.common.ext.getActivity
import com.hopeinyang.zeroknowledge.data.dao.LocationService
import com.hopeinyang.zeroknowledge.data.dto.Response
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject

class LocationUpdates @Inject constructor (
    private val context: Context
) : LocationService{

    @SuppressLint("MissingPermission")
    override fun getUserCurrentLocation(): Flow<Response<Location?>> = callbackFlow {

        require(context.hasPermission()){throw RuntimeException("Permission not granted")}

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val locationCallback = object :LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
                 trySend(Response.success(locationResult.lastLocation))



        }

        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            super.onLocationAvailability(locationAvailability)
            if (!locationAvailability.isLocationAvailable){
                trySend(Response.error("location not available", null))
            }

        }

    }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback
            ,
            context.mainLooper
        )
        awaitClose { fusedLocationClient.removeLocationUpdates(locationCallback) }


    }

    override fun enabledLocationRequest(context: Context, ) {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { response->

        }
            .addOnFailureListener { exception->
                if (exception is ResolvableApiException){

                    try {
                           context.getActivity()?.let { exception.startResolutionForResult(it,100) }

                    }catch (sendEx: IntentSender.SendIntentException){


                    }
                }
            }
    }

    override fun isConnected(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    }


    private fun Context.hasPermission():Boolean{
        return this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    }






    companion object{
        val ONE_MINUTE: Long = 10000
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, ONE_MINUTE)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(ONE_MINUTE/4)
            .build()
    }

}
package com.hopeinyang.zeroknowledge.data.dao.impl

import android.content.Context
import com.hopeinyang.zeroknowledge.data.dao.TrustScoreService
import com.hopeinyang.zeroknowledge.utils.DistanceCalculator


import com.hopeinyang.zeroknowledge.utils.isDeviceADBEnabled
import com.hopeinyang.zeroknowledge.utils.isDeviceScreenLocked
import timber.log.Timber
import javax.inject.Inject


class TrustScoreImpl @Inject constructor(
    private val context: Context
) : TrustScoreService {

    private val distanceCalculator = DistanceCalculator()


    val workTime = true



    override fun getTrustScore(
        p0: Pair<Double, Double>,
        p1:Pair<Double, Double>,
        mfaEnable:Boolean
    ): Int {
        val adbEnabled = isDeviceADBEnabled(context)
        val distanceFromOffice = distanceCalculator.calcDistance(p0, p1)



        val locationInRange = distanceFromOffice <= 0.5

        //Timber.e("Distance score: $distanceFromOffice")

        return computeTrustScore(adbEnabled, locationInRange, workTime, mfaEnable)

    }

    private fun computeTrustScore(
        adbEnabled:Boolean,
        locationInRange:Boolean,
        isWorkTime:Boolean,
        mfaEnable:Boolean
    ):Int{

        val trustScore = when {
            !adbEnabled && locationInRange && isWorkTime && mfaEnable -> 10// ABCD
            !adbEnabled && locationInRange && isWorkTime && !mfaEnable -> 3// ABC
            !adbEnabled && locationInRange && !isWorkTime && mfaEnable -> 6// ABD
            !adbEnabled && !locationInRange && isWorkTime && mfaEnable -> 5// ACD
            adbEnabled && locationInRange && isWorkTime && mfaEnable -> 7 // BCD
            !adbEnabled && locationInRange && !isWorkTime && !mfaEnable -> 2 //AB
            !adbEnabled && !locationInRange && isWorkTime && !mfaEnable -> 3 // AC
            !adbEnabled && !locationInRange && !isWorkTime && mfaEnable -> 5 // AD
            adbEnabled && locationInRange && isWorkTime && !mfaEnable -> 3 // BC
            adbEnabled && locationInRange && !isWorkTime && mfaEnable -> 6 //BD
            adbEnabled && !locationInRange && isWorkTime && mfaEnable -> 5 // CD
            !adbEnabled && !locationInRange && !isWorkTime && !mfaEnable -> 2//A
            adbEnabled && locationInRange && !isWorkTime && !mfaEnable -> 3//B
            adbEnabled && !locationInRange && isWorkTime && !mfaEnable -> 2//C
            adbEnabled && !locationInRange && !isWorkTime && mfaEnable -> 4//D

            else -> 0
        }

       return trustScore
   }



}
package com.hopeinyang.zeroknowledge.data.dao

import android.location.Location

interface TrustScoreService {

    fun getTrustScore(p0:Pair<Double, Double>, p1:Pair<Double, Double>, mfaEnable:Boolean):Int
}
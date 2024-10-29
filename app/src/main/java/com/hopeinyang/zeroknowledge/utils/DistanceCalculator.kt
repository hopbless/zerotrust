package com.hopeinyang.zeroknowledge.utils

import com.google.type.LatLng
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class DistanceCalculator() {

    companion object {
        val EARTH_RADIUS_IN_MILES = 3956.0
        val EARTH_RADIUS_IN_KILOMETER = 6367.0
        val EARTH_RADIUS_IN_METERS = EARTH_RADIUS_IN_KILOMETER*1000
    }

    private fun toRadian(`val`: Double): Double {
        return `val` * (Math.PI / 180)
    }

    private fun toDegree(`val`: Double): Double {
        return `val` * 180 / Math.PI
    }

    private fun diffRadian(val1: Double, val2: Double): Double {
        return toRadian(val2) - toRadian(val1)
    }

    fun calcDistance(p1:Pair<Double, Double>, p2: Pair<Double, Double>): Double {
        return calcDistance(
            p1.first,
            p1.second,
            p2.first,
            p2.second,
            EARTH_RADIUS_IN_KILOMETER
        )
    }

    fun calcBearing(p1: Pair<Double, Double>, p2: Pair<Double, Double>): Double {
        return calcBearing(p1.first, p1.second, p2.first, p2.second)
    }

    private fun calcBearing(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        run {
            val dLat = lat2 - lat2
            var dLon = lng2 - lng1
            val dPhi: Double = ln( tan(lat2 / 2 + Math.PI / 4) / tan(lat1 / 2 + Math.PI / 4) )
            val q: Double =
                if (abs(dLat) > 0) dLat / dPhi else cos(lat1)
            if (abs(dLon) > Math.PI) {
                dLon = if (dLon > 0) -(2 * Math.PI - dLon) else 2 * Math.PI + dLon
            }
            //var d = Math.Sqrt(dLat * dLat + q * q * dLon * dLon) * R;
            return toDegree(atan2(dLon, dPhi))
        }
    }

    private fun calcDistance(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double,
        radius: Double
    ): Double {
        return radius * 2 * asin(
            1.0.coerceAtMost(
                sqrt(
                    sin(
                        diffRadian(lat1, lat2) / 2.0
                    ).pow(2.0)
                            + cos(toRadian(lat1)) * cos(toRadian(lat2)) * sin(
                        diffRadian(lng1, lng2) / 2.0
                    ).pow(2.0)
                )
            )
        )
    }

}

/*
Math.min(
1.0, sqrt(
sin(
DiffRadian(lat1, lat2) / 2.0
).pow(2.0)
+ cos(ToRadian(lat1)) * cos(ToRadian(lat2)) * sin(
DiffRadian(lng1, lng2) / 2.0
).pow(2.0)
)
)*/

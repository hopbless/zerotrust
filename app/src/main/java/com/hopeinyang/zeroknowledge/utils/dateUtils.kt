package com.hopeinyang.zeroknowledge.utils

import android.icu.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

fun getCurrentDateTimeStamp(localDate: LocalDate):String{
    return localDate.atStartOfDay(ZoneId.of("UTC")).toInstant().epochSecond.toString()
}

fun getLocalDateFromTimeStamp(timeStamp:String):String{
    val dt = Instant.ofEpochSecond(timeStamp.toLong()).atZone(ZoneId.systemDefault()).toLocalDate()
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    return formatter.format(dt)

}
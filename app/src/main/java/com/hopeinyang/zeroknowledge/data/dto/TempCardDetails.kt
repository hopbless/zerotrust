package com.hopeinyang.zeroknowledge.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TempCardDetails(
    val cardDetails: HashMap<String, String> = hashMapOf()
):Parcelable

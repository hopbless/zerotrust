package com.hopeinyang.zeroknowledge.data.dto

import android.os.Parcelable
import com.google.firebase.auth.MultiFactorResolver
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import kotlinx.parcelize.Parcelize

@Parcelize
data class OtpAuthResult(
    val isSuccessful: Boolean = false,
    val smsId: String = "",
    val from:String ="",
    val phoneNumber:String = "",
    val multiFactorResolver: MultiFactorResolver? = null,
    val resendingToken: ForceResendingToken = ForceResendingToken.zza(),
    val password:String =""
):Parcelable

package com.hopeinyang.zeroknowledge.ui.screens.OtpScreen

data class OtpUiState(
    val isOtpFilled: Boolean = false,
    val otpValue:String ="",
    val phoneNumber:String = "",
    val isTimerActive:Boolean = false,
    val enableSMSButton:Boolean = true,
    val userId:String = "",
    val verificationId:String = "",
    val navigateFrom:String ="",
    val otpResendRequest:Boolean = false,
    val otpSubTitle:String = ""
){

}

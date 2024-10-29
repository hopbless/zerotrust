package com.hopeinyang.zeroknowledge.ui.screens.ReverificationScreen

data class ReverificationUiState(
    val userId:String ="",
    val userEmail:String="",
    val pinText:String = "",
    val userEncryptedPin:String = "",
    val userEncryptedPassword:String ="",
    val isUserPinEnabled:Boolean = false,
    val isFingerprintsEnabled:Boolean = false,
    val secondAuthStatus:String = "Disabled",
    val isLoginInProgress:Boolean = false,
    val phoneNumber:String ="",
    val isSecondAuthEnable:Boolean = false,
    val networkStatus:String = ""
)

package com.hopeinyang.zeroknowledge.ui.screens.Settings

data class SettingUiState(
    val firstName:String ="",
    val lastName:String ="",
    val phoneNumber:String ="",
    val userId:String ="",
    val isMFASwitch:Boolean = false,
    val smsId:String ="",
    val isBiometricEnabled:Boolean = false,
    val userEmail:String = "",
    val userImageUrl:String="",
    val department:String ="",
    val isPinModeEnabled:Boolean = false,
    val isSecondAuthEnabled:Boolean = false,
    val userPassword:String =""

)

package com.hopeinyang.zeroknowledge.ui.screens.AddPinScreen

data class AddPinUIState(
    val pinText:String = "",
    val confirmPinText:String = "",
    val hideKeyboard:Boolean = false,
    val enableButton:Boolean = false,
    val userId:String = ""
)


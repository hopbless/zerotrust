package com.hopeinyang.zeroknowledge.ui.screens.SplashScreen

data class SplashUiState(
    val showError:Boolean = false,
    val continueButtonClicked:Boolean = false,
    val userId:String = "",
    val hasUser:Boolean = false,
    val isGranted:Boolean = false
){

}

package com.hopeinyang.zeroknowledge.ui.screens.Login

data class LoginUiState(
    val email:String = "",
    val password:String = "",
    val useFingerPrint:Boolean = false,
    val isLoginInProgress:Boolean = false
)

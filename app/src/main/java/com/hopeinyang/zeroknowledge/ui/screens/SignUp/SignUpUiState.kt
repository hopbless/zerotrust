package com.hopeinyang.zeroknowledge.ui.screens.SignUp

data class SignUpUiState(
    val firstName:String = "",
    val lastName:String = "",
    val phoneNumber:String = "",
    val email:String = "",
    val password:String ="",
    val age:String ="",
    val repeatPass:String ="",
    val checkBoxValue:Boolean = false,
    val phoneCode:String ="",
    val isLoginInProgress:Boolean = false,

)

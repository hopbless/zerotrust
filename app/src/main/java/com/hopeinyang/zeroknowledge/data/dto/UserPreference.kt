package com.hopeinyang.zeroknowledge.data.dto

data class UserPreference(
    var multifactorStatus: String = "Disabled",
    var userEmail: String = "",
    var userPassword: String = "",
    var userPin: String = "",
    var isVerified: Boolean = false,
    var pinModeEnabled: Boolean = false,
    var fingerPrintEnabled: Boolean = false,
    var userId:String = "",
    var department:String ="",
    var specialty:String ="",
    var isAdmin:Boolean = false

)

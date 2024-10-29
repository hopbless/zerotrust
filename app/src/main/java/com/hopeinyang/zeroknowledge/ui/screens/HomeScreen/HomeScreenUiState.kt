package com.hopeinyang.zeroknowledge.ui.screens.HomeScreen

import com.hopeinyang.zeroknowledge.data.dto.HospitalInfo
import com.hopeinyang.zeroknowledge.data.dto.ViewPagerContents

data class HomeScreenUiState(
    val menuClicked:Boolean = false,
    val isEmailVerified:Boolean = false,
    val emailAddress:String = "",
    val homeScreenContent:List<ViewPagerContents>? = null,
    val showViewPager:Boolean = true,
    val secondAuthFactorEnable:Boolean = false,
    val phoneNumber:String="",
    val userId:String="",
    val showSecondAuthDialog:Boolean = false,
    val isPinModeEnable:Boolean = false,
    val isFingerprintEnabled:Boolean = false,
    val destination:String ="",
    val showModalSheet:Boolean = false,
    val pinText:String = "",
    val encryptedPin :String = "",
    val firstName:String = "",
    val lastName:String ="",
    val loggedInTime:String ="",
    val trustScore:Int =0,
    val imageUrl:String ="",
    val hospitalLocation: HashMap<String, String> = hashMapOf(),
    val isGPSEnabled:Boolean = false,
    val userLocation:Pair<Double, Double> = Pair(0.0, 0.0),
    val department:String ="",
    val role:String = "",
    val showIndicator:Boolean = false,
    val isAdminUser:Boolean = false
)

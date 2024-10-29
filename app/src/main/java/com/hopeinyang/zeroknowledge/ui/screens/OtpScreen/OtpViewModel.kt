package com.hopeinyang.zeroknowledge.ui.screens.OtpScreen

import android.content.Context
import androidx.compose.runtime.mutableStateOf

import androidx.lifecycle.viewModelScope
import com.hopeinyang.zeroknowledge.HOME_SCREEN

import com.hopeinyang.zeroknowledge.LOGIN_SCREEN
import com.hopeinyang.zeroknowledge.MainViewModel
import com.hopeinyang.zeroknowledge.OTP_SCREEN
import com.hopeinyang.zeroknowledge.SETTINGS_SCREEN
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarManager
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarMessage
import com.hopeinyang.zeroknowledge.data.dto.OtpAuthResult

import com.hopeinyang.zeroknowledge.data.dao.AccountService
import com.hopeinyang.zeroknowledge.data.dao.EncryptionService
import com.hopeinyang.zeroknowledge.data.dao.LogService
import com.hopeinyang.zeroknowledge.data.dao.StorageService

import com.hopeinyang.zeroknowledge.data.dao.UserPreferenceService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(

    private val logService: LogService,
    private val accountService: AccountService,
    private val dataStore:UserPreferenceService,
    private val storageService: StorageService,
    private val encryptionService: EncryptionService,

):MainViewModel(logService,  accountService, storageService , dataStore) {



    var uiState = mutableStateOf(OtpUiState())
        private set


    private val userId = accountService.currentUserId

    private val navigateFrom
        get() = uiState.value.navigateFrom

    private val verificationId
        get() = uiState.value.verificationId


    init {

        val userId = accountService.currentUserId
        uiState.value = uiState.value.copy(
            userId = userId,

        )


    }

    fun onOtpValueChange(otp: String) {

            uiState.value = uiState.value.copy(
                otpValue = otp,
                enableSMSButton = otp.isEmpty()
            )


    }

    fun isOtpFilled(isOtpFilled:Boolean){
        uiState.value = uiState.value.copy(
            isOtpFilled = isOtpFilled
        )

    }

    fun navigateBack(openAndPopUp: (String, String) -> Unit) {
        val currentUserId = accountService.currentUserId
        when (navigateFrom) {
            SETTINGS_SCREEN -> {
                openAndPopUp("$HOME_SCREEN/$currentUserId", OTP_SCREEN)
            }
            LOGIN_SCREEN -> {
                openAndPopUp(LOGIN_SCREEN, OTP_SCREEN)
            }
            else -> {
                openAndPopUp(LOGIN_SCREEN, OTP_SCREEN)
            }
        }
    }

    fun setTimerActive(timerActive: Boolean) {
        uiState.value = uiState.value.copy(
            isTimerActive = timerActive,
            enableSMSButton = !timerActive
        )


    }


    fun verifySMSCode(
        otpValue: String,
        authResult: OtpAuthResult?,
        openAndPopUp: (String, String) -> Unit
    ) {

        uiState.value = uiState.value.copy(
            navigateFrom = authResult?.from ?: "",
            verificationId = authResult?.smsId ?: "",
            )

        Timber.e("navigate from is: ${uiState.value.navigateFrom}")
        Timber.e("from is: ${authResult?.from} and id: ${authResult?.smsId}")
        if (navigateFrom == SETTINGS_SCREEN) {

            uiState.value = uiState.value.copy(
                phoneNumber = authResult?.phoneNumber ?: "",

                )

            if (verificationId.isNotEmpty())
                viewModelScope.launch {
                    accountService.verifySMSCode(verificationId, otpValue,){
                            isSuccessful->
                        if (isSuccessful){
                            launchCatching {
                                dataStore.updateMultifactorStatus(userId)
                                storageService.updateUserRecord(
                                    userId,
                                    mapOf("secondAuth" to userId)
                                ){isSuccessful->

                                    if (isSuccessful){

                                        openAndPopUp("$HOME_SCREEN/$userId", OTP_SCREEN)
                                    }
                                }
                            }


                        }else{
                            SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Something went, try again later"))
                            openAndPopUp("$HOME_SCREEN/$userId", OTP_SCREEN)
                        }


                    }
                }
        }else if (navigateFrom == LOGIN_SCREEN){
            //Timber.d("Show smsId ${uiState.value.verificationId}")
            if (authResult?.multiFactorResolver != null){
                val smsId = uiState.value.verificationId

                val password = authResult.password
                viewModelScope.launch {
                    accountService.authenticateSecondFactor(
                        smsId,
                        otpValue,
                        authResult.multiFactorResolver
                    ){ isLogin->
                        if (isLogin){
                            launchCatching (
                                onError = {SnackBarManager.showMessage(
                                    SnackBarMessage.StringSnackbar(
                                        "Empty password"
                                    )
                                )}
                            ) {



                                if (password.isNotEmpty()){
                                    //Timber.e("My password is: ${password}")
                                    val encryptedPassword = requireNotNull(encryptionService.encryptText(password)){
                                        throw IllegalArgumentException("Encrypted password is null")
                                    }
                                    val currentUser = accountService.currentUser
                                    reloadUserPreferences(currentUser, OTP_SCREEN, encryptedPassword, openAndPopUp)
                                }else{
                                    val currentUser = accountService.currentUser
                                    reloadUserPreferences(currentUser, OTP_SCREEN, null, openAndPopUp)
                                }

                                //openAndPopUp("$DASHBOARD_SCREEN/${currentUser.id}", OTP_SCREEN)
                            }






                        }else{
                            openAndPopUp(LOGIN_SCREEN, OTP_SCREEN)
                        }

                    }


                }
            }else{
                SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Something went wrong, try gain later"))
                Timber.d("MultifactorResolve is Null")
                openAndPopUp(LOGIN_SCREEN, OTP_SCREEN)
            }
        }else if(uiState.value.otpResendRequest){

            viewModelScope.launch {
                accountService.verifySMSCode(
                    uiState.value.verificationId,
                    otpValue,
                ){ isSuccessful->
                    if (isSuccessful){
                        launchCatching {
                            storageService.updateUserRecord(
                                userId,
                                mapOf("secondAuth" to userId)
                            ){isSuccessful->
                                if (isSuccessful){
                                    openAndPopUp("$HOME_SCREEN/$userId", OTP_SCREEN)
                                }
                            }
                        }


                    }else if(uiState.value.navigateFrom == LOGIN_SCREEN){
                        openAndPopUp(LOGIN_SCREEN, OTP_SCREEN)
                    }

                    openAndPopUp("$HOME_SCREEN/$userId", OTP_SCREEN)

                }
            }

        } else{
            SnackBarManager
                .showMessage(SnackBarMessage
                    .StringSnackbar("Something went wrong, try gain later"))
            openAndPopUp(LOGIN_SCREEN, OTP_SCREEN)
        }

    }

    fun resendSMS(context: Context) {
        launchCatching (snackbar = true){
            //val smsId = accountService.sendSMS(context,uiState.value.phoneNumber)
//            if (smsId.isNotEmpty()){
//                uiState.value = uiState.value.copy(
//                    verificationId = smsId
//                )
//            }
        }
    }

    fun updateOtpState(otpAuthResult: OtpAuthResult?) {
        val text = if (otpAuthResult?.phoneNumber != null && otpAuthResult.phoneNumber.isNotEmpty()){
            "Please verify your phone number: ${otpAuthResult.phoneNumber} with the OTP sent to you"
        }else "Please verify your phone number with the OTP sent to you"


        uiState.value = uiState.value.copy(
            navigateFrom = otpAuthResult?.from ?: "",
            verificationId = otpAuthResult?.smsId ?: "",
            otpSubTitle = text,
            phoneNumber = otpAuthResult?.phoneNumber ?: ""

        )
    }


}
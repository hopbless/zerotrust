package com.hopeinyang.zeroknowledge.ui.screens.Settings

import android.content.Context
import com.hopeinyang.zeroknowledge.ACCOUNT_SCREEN
import com.hopeinyang.zeroknowledge.ADD_PIN_SCREEN
import com.hopeinyang.zeroknowledge.HOME_SCREEN
import com.hopeinyang.zeroknowledge.LOGIN_SCREEN
import com.hopeinyang.zeroknowledge.MainViewModel
import com.hopeinyang.zeroknowledge.OTP_SCREEN
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.SETTINGS_SCREEN
import com.hopeinyang.zeroknowledge.common.ext.isValidFirstName
import com.hopeinyang.zeroknowledge.common.ext.isValidLastName
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarManager
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarMessage
import com.hopeinyang.zeroknowledge.data.dao.AccountService
import com.hopeinyang.zeroknowledge.data.dao.EncryptionService
import com.hopeinyang.zeroknowledge.data.dao.LogService
import com.hopeinyang.zeroknowledge.data.dao.StorageService
import com.hopeinyang.zeroknowledge.data.dao.UserPreferenceService
import com.hopeinyang.zeroknowledge.data.dao.impl.BiometricManagerPrompt
import com.hopeinyang.zeroknowledge.data.dto.OtpAuthResult

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import timber.log.Timber

import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    logService: LogService,
    private val accountService: AccountService,
    private val storageService: StorageService,
    private val dataStore: UserPreferenceService,

):MainViewModel(logService,  accountService, storageService , dataStore) {

    private val  _state = MutableStateFlow(SettingUiState())
    val state:StateFlow<SettingUiState>
        get() = _state


    private val firstName
        get() = _state.value.firstName

    private val lastName
        get() = _state.value.lastName


    private val userId
        get() = _state.value.userId

    private val phoneNumber
        get() = _state.value.phoneNumber




    init {
        launchCatching (snackbar = true) {
            val userId = accountService.currentUserId
            val userPrefFlow = dataStore.getUserPreferences()
            val userInfoFlow =   storageService.getUserInfoFlow(userId)
            val currentUser = accountService.currentUser

            combine(userPrefFlow, userInfoFlow,){pref, userInfo,  ->

                SettingUiState(
                    firstName = userInfo.data?.firstName ?: "",
                    lastName = userInfo.data?.lastName ?: "",
                    phoneNumber = if (currentUser.phoneNumber.isEmpty()) userInfo.data?.phoneNumber ?: "" else "",
                    userId = currentUser.userId,
                    isSecondAuthEnabled = currentUser.secondAuth == currentUser.userId || currentUser.userId == pref.multifactorStatus,
                    isBiometricEnabled = pref.fingerPrintEnabled,
                    isPinModeEnabled = pref.pinModeEnabled,
                    userEmail = currentUser.email,
                    department = userInfo.data?.department ?: "",
                    userPassword = pref.userPassword

                )
            }.collect{_state.value = it}




        }

    }


    fun onSignOutClick(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.signOut()
            restartApp(LOGIN_SCREEN)
        }
    }

    fun onDeleteMyAccountClick(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.deleteAccount()
            restartApp(LOGIN_SCREEN)
        }
    }

    fun onFirstNameChange(firstName: String) {

        _state.value = _state.value.copy(
            firstName = firstName
        )
    }

    fun onLastNameChange(lastName: String) {
        _state.value = _state.value.copy(
            lastName = lastName
        )
    }



    fun onPhoneNumberChange(phoneNumber:String){
        _state.value = _state.value.copy(
            phoneNumber = phoneNumber
        )
    }

    fun onUpdateClick(openAndPop: (String, String)->Unit){

        if (!firstName.isValidFirstName()) {
            SnackBarManager.showMessage(R.string.first_name_error)
            return
        }

        if (!lastName.isValidLastName()) {
            SnackBarManager.showMessage(R.string.last_name_error)
            return
        }

        if (phoneNumber.isEmpty() || phoneNumber.length<12 || phoneNumber[0] !='+'){
            SnackBarManager.showMessage(SnackBarMessage
                .StringSnackbar("Make sure you enter correct phone number starting with your phone code"))
            return
        }



        launchCatching (snackbar = true){

            val userUpdate = mapOf(
                "firstName" to _state.value.firstName,
                "lastName" to _state.value.lastName,
                "phoneNumber" to phoneNumber
            )
            storageService.updateUserRecord(_state.value.userId, userUpdate){isSuccessful->
                if (isSuccessful)
                    SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Update Successful"))
                else SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Update Failed"))
            }

            //preHealthRepository.updateUserInfo(userInfoUpdate)
            openAndPop("$HOME_SCREEN/$userId", SETTINGS_SCREEN)
        }


    }

    fun onEditAccount(openAndNavigate:(String)->Unit) {
        openAndNavigate(ACCOUNT_SCREEN)
    }

    fun navigateBack(openAndPop: (String, String) -> Unit) {
        openAndPop(SETTINGS_SCREEN, ACCOUNT_SCREEN)
    }

    fun navigateBackToHome(openAndPop: (String, String) -> Unit){
        openAndPop("$HOME_SCREEN/$userId", SETTINGS_SCREEN)
    }

    fun onMFASwitchChange(isSwitch: Boolean) {
        if (!_state.value.isSecondAuthEnabled) {
            _state.value = _state.value.copy(
                isMFASwitch = isSwitch
            )
        }
    }

    fun onEnableMFA(context: Context,phoneNumber: String, navigateToOtp: (String, OtpAuthResult) -> Unit){
        if (phoneNumber.isNotEmpty() && phoneNumber.length > 12){

            launchCatching (snackbar = true) {

                accountService.sendSMS(
                    context,
                    phoneNumber,
                    from = SETTINGS_SCREEN,

                    ){isSuccessful, message, otpAuthResult->

                        if (isSuccessful && message == "SMSCodeSent") {
                            //Timber.e("Auth Result ${otpAuthResult?.from} and id ${otpAuthResult?.smsId}")
                            navigateToOtp(OTP_SCREEN, otpAuthResult!!)
                    } else {

                        SnackBarManager
                            .showMessage(
                                SnackBarMessage
                                    .StringSnackbar(message)
                            )
                        _state.value = _state.value.copy(
                            isMFASwitch = false
                        )
                    }

                }
            }






        }else{
            SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Update your phone in the settings"))
        }

    }

    fun onBiometricSwitchChange(isChecked: Boolean, promptManager: BiometricManagerPrompt) {
        if (!_state.value.isBiometricEnabled && isChecked){

            promptManager.showBiometricPrompt("Enroll", "Add fingerprint to your app" )

        }else if(_state.value.isBiometricEnabled && !isChecked){
            promptManager.showBiometricPrompt("Enroll", "" )
        }
    }



    fun updateBiometricStatus() {
        if (!_state.value.isBiometricEnabled){
            launchCatching {
                dataStore.updateFingerPrint(true)
                _state.value = _state.value.copy(
                    isBiometricEnabled = true
                )
            }
        }else{
            launchCatching {
                dataStore.updateFingerPrint(false)
                _state.update {
                    it.copy(
                        isBiometricEnabled = false
                    )
                }
            }
        }

    }

    fun onPinModeEnabledChange(isChecked: Boolean, openAndNavigate: (String) -> Unit) {

        if (!_state.value.isPinModeEnabled) {
            _state.value = _state.value.copy(
                isPinModeEnabled = isChecked
            )
            openAndNavigate("$ADD_PIN_SCREEN/$userId")
        }

    }





}
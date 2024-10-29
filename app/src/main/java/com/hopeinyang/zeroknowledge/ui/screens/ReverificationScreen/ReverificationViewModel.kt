package com.hopeinyang.zeroknowledge.ui.screens.ReverificationScreen

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.hopeinyang.zeroknowledge.HOME_SCREEN
import com.hopeinyang.zeroknowledge.LOGIN_SCREEN
import com.hopeinyang.zeroknowledge.MainViewModel
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarManager
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarMessage
import com.hopeinyang.zeroknowledge.data.dao.AccountService
import com.hopeinyang.zeroknowledge.data.dao.ConnectivityObserver
import com.hopeinyang.zeroknowledge.data.dao.EncryptionService
import com.hopeinyang.zeroknowledge.data.dao.LogService
import com.hopeinyang.zeroknowledge.data.dao.StorageService
import com.hopeinyang.zeroknowledge.data.dao.UserPreferenceService
import com.hopeinyang.zeroknowledge.data.dto.OtpAuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReverificationViewModel @Inject constructor(
    logService: LogService,
    private val accountService: AccountService,
    private val storageService: StorageService,
    private val dataStore: UserPreferenceService,
    private val encryptionService: EncryptionService,
    savedStateHandle: SavedStateHandle,
    private val connectivityObserver: ConnectivityObserver

) :MainViewModel(logService, accountService, storageService , dataStore,){

    private val currentUserId = checkNotNull(savedStateHandle.get<String>("userId"))

    private var _state = MutableStateFlow(ReverificationUiState())
    val state: StateFlow<ReverificationUiState>
        get() = _state


    init {
        val userPreferencesFlow = dataStore.getUserPreferences()
        val userInfoFlow = storageService.getUserInfoFlow(currentUserId)
        val currentUser = accountService.currentUser
        val internetStatus = connectivityObserver.observer()
        launchCatching {

            combine(userPreferencesFlow, userInfoFlow, internetStatus ){ userPref, userInfo, net ->


                ReverificationUiState(
                    userEncryptedPin = userPref.userPin,
                    secondAuthStatus = userPref.multifactorStatus,
                    isUserPinEnabled = userPref.pinModeEnabled,
                    isFingerprintsEnabled = userPref.fingerPrintEnabled,
                    userId = currentUserId,
                    phoneNumber = userInfo.data?.phoneNumber ?: "",
                    userEncryptedPassword = userPref.userPassword,
                    userEmail = userPref.userEmail,
                    isSecondAuthEnable = currentUser.secondAuth == currentUserId || userPref.multifactorStatus == currentUserId,
                    networkStatus = net.toString(),


                    )

            }.collect{_state.value = it}


        }
    }


    fun onPinChange(pin: String) {
        _state.value = _state.value.copy(
            pinText = pin
        )
    }

    fun onPinEntered(pin: String, clearAndNavigate:(String)->Unit) {

        _state.value = _state.value.copy(
            isLoginInProgress = true
        )
        launchCatching (snackbar = true){

            val encryptedPin = _state.value.userEncryptedPin

            if (encryptedPin.isNotEmpty()){
                val decryptedPin  = encryptionService.decryptText(encryptedPin)

                if (decryptedPin.contentEquals(pin)){
                    if (_state.value.userId.isNotEmpty()){
                        clearAndNavigate("$HOME_SCREEN/${_state.value.userId}")
                    }else{
                        SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Something went wrong, check you network connectivity"))
                    }

                }else{
                    _state.value = _state.value.copy(
                        isLoginInProgress = false
                    )
                    SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Pin Incorrect, try again"))
                }

            }else{
                _state.value = _state.value.copy(
                    isLoginInProgress = false
                )
                SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Pin Incorrect, try again"))
            }

        }

    }

    fun onOtpLoginClick(
        context: Context,
        clearAndNavigate: (String) -> Unit,
        navigateToOtpScreen: (String, OtpAuthResult?) -> Unit
    ) {

        if (_state.value.userId == _state.value.secondAuthStatus){

            launchCatching (snackbar = true) {

                val password = requireNotNull(encryptionService.decryptText(_state.value.userEncryptedPassword)){throw IllegalArgumentException("Encrypted password is null")}

                val email = requireNotNull(_state.value.userEmail){throw IllegalArgumentException("Email is null")}
                //Timber.e("Password is: $password and email is: $email")
                accountService.authenticate(
                    context,
                    password = password,
                    email = email,
                    navigateTo = navigateToOtpScreen
                ){ isLogin, message->
                    if (isLogin){
                        //Timber.e("EncryptedPassword is: $password")
                        clearAndNavigate("$HOME_SCREEN/${_state.value.userId}")
                    }else{
                        SnackBarManager.showMessage(SnackBarMessage.StringSnackbar(message))
                        clearAndNavigate(LOGIN_SCREEN)
                    }

                }


            }


        }else{
            SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("It seems you have not yet enroll a 2FA"))
        }
    }

}
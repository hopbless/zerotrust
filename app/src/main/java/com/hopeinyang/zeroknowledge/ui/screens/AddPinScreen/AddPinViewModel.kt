package com.hopeinyang.zeroknowledge.ui.screens.AddPinScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import com.hopeinyang.zeroknowledge.HOME_SCREEN
import com.hopeinyang.zeroknowledge.MainViewModel
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.common.ext.isPinValid
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarManager
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarMessage
import com.hopeinyang.zeroknowledge.data.dao.AccountService
import com.hopeinyang.zeroknowledge.data.dao.EncryptionService
import com.hopeinyang.zeroknowledge.data.dao.LogService
import com.hopeinyang.zeroknowledge.data.dao.StorageService
import com.hopeinyang.zeroknowledge.data.dao.UserPreferenceService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class AddPinViewModel @Inject constructor(
    logService: LogService,
    private val accountService: AccountService,
    private val dataStore: UserPreferenceService,
    private val encryptionService: EncryptionService,
    private val storageService: StorageService,
    savedStateHandle: SavedStateHandle,
) : MainViewModel(logService, accountService, storageService , dataStore ){



    private val currentUserId = checkNotNull(savedStateHandle.get<String>("userId")){throw IllegalArgumentException("userId cannot be null")}

    var state = mutableStateOf(AddPinUIState())
        private set


    init {
        state.value = state.value.copy(userId = currentUserId)
    }


    fun navigateBack(clearAndNavigate: (String)->Unit) {

        clearAndNavigate("$HOME_SCREEN/$currentUserId")

    }


    fun onPinChange(pin: String){
        state.value = state.value.copy(pinText = pin)
    }

    fun onConfirmPinChange(confirmPin: String){
        state.value = state.value.copy(confirmPinText = confirmPin)
    }

    fun onPinComplete(confirmPin: String) {
        state.value = state.value.copy(hideKeyboard = true)
        state.value = state.value.copy(enableButton = true)

    }

    fun onContinueButtonClick(clearAndNavigate: (String) -> Unit){

        val pinText = state.value.pinText
        val confirmPin = state.value.confirmPinText

        if (!confirmPin.isPinValid(pinText)){
            SnackBarManager.showMessage(R.string.pin_mismatch)
            return
        }

        launchCatching {

            val encryptedPin = encryptionService.encryptText(confirmPin)
            if (encryptedPin !=null){
                dataStore.updatePin(encryptedPin)
                dataStore.updatePinMode(true)

                SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Encrypted Pin Created Successfully"))

            }else{
                SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Pin Encryption Failed, try again later"))
            }

            clearAndNavigate("$HOME_SCREEN/$currentUserId")
        }




    }



}
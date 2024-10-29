package com.hopeinyang.zeroknowledge.ui.screens.Login

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.hopeinyang.zeroknowledge.LOGIN_SCREEN
import com.hopeinyang.zeroknowledge.MainViewModel
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.SIGNUP_SCREEN
import com.hopeinyang.zeroknowledge.common.ext.isValidEmail
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarManager
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
class LoginViewModel  @Inject constructor(
    private val logService: LogService,
    private val accountService: AccountService,
    private val storageService: StorageService,
    private val encryptionService: EncryptionService,
    private val dataStore:UserPreferenceService
):MainViewModel(logService,  accountService, storageService , dataStore) {

    var uiSate = mutableStateOf(LoginUiState())
        private set



    private val email
        get() = uiSate.value.email

    private val password
        get() = uiSate.value.password


    fun onEmailChange(email:String){
        uiSate.value = uiSate.value.copy(
            email = email
        )
    }

    fun onPasswordChange(password:String){
        uiSate.value = uiSate.value.copy(
            password = password
        )
    }

    fun onLoginClick(
        context: Context,
        navigateTo:(String, OtpAuthResult)-> Unit,
        openAndPopUp:(String, String)->Unit
    ){

        if (!email.isValidEmail()) {
            SnackBarManager.showMessage(R.string.email_error)
            return
        }

        if (password.isBlank()) {
            SnackBarManager.showMessage(R.string.empty_password_error)
            return
        }

        viewModelScope.launch {
            uiSate.value = uiSate.value.copy(isLoginInProgress = true)

            accountService.authenticate(
                context,
                email,
                password,
                navigateTo,

            ){isSucess, message->
                if (isSucess){
                    launchCatching (
                        snackbar = true,
                        onError = {
                            throwable ->
                            //Timber.e("Login error $throwable")
                            if (throwable != null) uiSate.value = uiSate.value.copy(isLoginInProgress = false)
                        }
                        ){
                        val encryptedPassword = encryptionService.encryptText(password)
                        //Timber.d("Encrypted Password is: $encryptedPassword")
                        val currentUser = accountService.currentUser
                        reloadUserPreferences(currentUser, LOGIN_SCREEN,encryptedPassword, openAndPopUp)
                    }
                }else{
                    uiSate.value = uiSate.value.copy(isLoginInProgress = false)
                }

            }


        }
    }


    fun onRegisterClick(loginText:String, openAndPopUp:(String, String)->Unit){
        openAndPopUp(SIGNUP_SCREEN, LOGIN_SCREEN)
    }
}
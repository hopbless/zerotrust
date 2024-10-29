package com.hopeinyang.zeroknowledge.ui.screens.SignUp


import androidx.compose.runtime.mutableStateOf
import com.hopeinyang.zeroknowledge.LOGIN_SCREEN

import com.hopeinyang.zeroknowledge.MainViewModel
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.SIGNUP_SCREEN
import com.hopeinyang.zeroknowledge.common.ext.isValidAge
import com.hopeinyang.zeroknowledge.common.ext.isValidEmail
import com.hopeinyang.zeroknowledge.common.ext.isValidFirstName
import com.hopeinyang.zeroknowledge.common.ext.isValidLastName
import com.hopeinyang.zeroknowledge.common.ext.isValidPassword
import com.hopeinyang.zeroknowledge.common.ext.isValidPhoneCode
import com.hopeinyang.zeroknowledge.common.ext.isValidPhoneNumber
import com.hopeinyang.zeroknowledge.common.ext.passwordMatches
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarManager
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarMessage

import com.hopeinyang.zeroknowledge.data.dto.UserInfo
import com.hopeinyang.zeroknowledge.data.dao.AccountService
import com.hopeinyang.zeroknowledge.data.dao.EncryptionService
import com.hopeinyang.zeroknowledge.data.dao.LogService
import com.hopeinyang.zeroknowledge.data.dao.StorageService

import com.hopeinyang.zeroknowledge.data.dao.UserPreferenceService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel@Inject constructor(
    logService: LogService,
    private val accountService: AccountService,
    private val storageService: StorageService,
    private val encryptionService: EncryptionService,
    private val dataStore: UserPreferenceService,

):MainViewModel(logService, accountService, storageService , dataStore) {
    var uiState = mutableStateOf(SignUpUiState())
        private set


    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password

    private val firstName
        get() = uiState.value.firstName

    private val lastName
        get() = uiState.value.lastName

    private val checkBoxValue
        get() = uiState.value.checkBoxValue



    private val phoneNumber
        get() = uiState.value.phoneNumber

    private val phoneCode
        get() = uiState.value.phoneCode


    //On Change Functions from UI
    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onRepeatPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(repeatPass = newValue)
    }


    fun onFirstNameChange(newValue: String) {
        uiState.value = uiState.value.copy(firstName = newValue)
    }

    fun onLastNameChange(newValue: String) {
        uiState.value = uiState.value.copy(lastName = newValue)
    }


    fun onPhoneNumberChange(newValue: String) {
        uiState.value = uiState.value.copy(phoneNumber = newValue)
    }

    fun onCheckBoxChange(newValue: Boolean) {
        uiState.value = uiState.value.copy(checkBoxValue = newValue )
    }


    fun onSignUpClick(clearAndNavigate: (String) -> Unit) {

        if (!email.isValidEmail()) {
            SnackBarManager.showMessage(R.string.email_error)
            return
        }

        if (!password.isValidPassword()) {
            SnackBarManager.showMessage(R.string.password_error)
            return
        }

        if (!password.passwordMatches(uiState.value.repeatPass)) {
            SnackBarManager.showMessage(R.string.password_match_error)
            return
        }


        if (!phoneCode.isValidPhoneCode()){

            SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Make sure to enter a correct phone code"))
            return
        }

        if (!phoneNumber.isValidPhoneNumber()) {
            SnackBarManager.showMessage(R.string.phone_number_error)
            return
        }



        if (!firstName.isValidFirstName()) {
            SnackBarManager.showMessage(R.string.first_name_error)
            return
        }

        if (!lastName.isValidLastName()) {
            SnackBarManager.showMessage(R.string.last_name_error)
            return
        }


        if (!checkBoxValue) {
            SnackBarManager.showMessage(R.string.accept_terms)
            return
        }



        launchCatching (
            snackbar = true,
            onError = {uiState.value = uiState.value.copy(isLoginInProgress = false)
            }
        ){

            uiState.value = uiState.value.copy(isLoginInProgress = true)

            launchCatching {

                accountService.createUserWithEmailAndPassword(
                    email,
                    password,

                     ) { isSuccessful ->
                    if (isSuccessful) {

                        val currentUser = accountService.currentUser
                        val userinfo = UserInfo(
                            email = email,
                            firstName = firstName,
                            lastName = lastName,
                            phoneNumber = phoneCode + phoneNumber,
                            userId = currentUser.userId,
                            isEmailVerified = currentUser.isEmailVerified
                        )
                        launchCatching(
                            snackbar = true,
                            onError = {
                                uiState.value = uiState.value.copy(isLoginInProgress = false)
                            }
                        ) {
                            val encryptedPassword = encryptionService.encryptText(password)


                            if (encryptedPassword != null) {
                                dataStore.updatePassword(encryptedPassword)
                                dataStore.updateEmail(email)
                            }

                            storageService.saveNewUser(userinfo, clearAndNavigate)
                        }

                    } else {
                        clearAndNavigate(LOGIN_SCREEN,)
                    }


                }
            }
        }

    }


    fun onPhoneCodeChange(code: String) {
        uiState.value = uiState.value.copy(phoneCode = code)
    }
}

package com.hopeinyang.zeroknowledge.ui.screens.SplashScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.firebase.auth.FirebaseAuthException
import com.hopeinyang.zeroknowledge.HOME_SCREEN
import com.hopeinyang.zeroknowledge.LOGIN_SCREEN
import com.hopeinyang.zeroknowledge.MainViewModel
import com.hopeinyang.zeroknowledge.REVERIFICATION_SCREEN
import com.hopeinyang.zeroknowledge.SIGNUP_SCREEN
import com.hopeinyang.zeroknowledge.SPLASH_SCREEN
import com.hopeinyang.zeroknowledge.data.dao.AccountService
import com.hopeinyang.zeroknowledge.data.dao.LogService
import com.hopeinyang.zeroknowledge.data.dao.StorageService
import com.hopeinyang.zeroknowledge.data.dao.UserPreferenceService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val logService: LogService,
    private val accountService: AccountService,
    private val storageService: StorageService,
    private val dataStore:UserPreferenceService

) : MainViewModel(logService, accountService, storageService , dataStore) {
    var uiState = mutableStateOf(SplashUiState())
        private set

    init {
        viewModelScope.launch {

            val hasUser = accountService.hasUser



            if(hasUser){
                val userId = accountService.currentUserId
                uiState.value = uiState.value.copy(
                    userId = userId,
                    hasUser = true
                )



            }

        }
    }


        fun onAppStart(
            openAndPopUp: (String, String) -> Unit,
        ){

            if (uiState.value.isGranted){
                if (uiState.value.hasUser){
                    val userId = uiState.value.userId
                    openAndPopUp("$REVERIFICATION_SCREEN/$userId",SPLASH_SCREEN,)

                } else{
                    openAndPopUp(LOGIN_SCREEN, SPLASH_SCREEN)

                }
            }



//            createAccount(openAndPopUp)
//            openAndPopUp(SIGNUP_SCREEN, SPLASH_SCREEN)

        }


    @OptIn(ExperimentalPermissionsApi::class)
    fun onPermissionResult(permission:List<PermissionState>, isGranted: Boolean, openAndPopUp: (String, String) -> Unit) {
        uiState.value = uiState.value.copy(
            isGranted = isGranted
        )

        if (isGranted){
            if (uiState.value.hasUser){
                val userId = uiState.value.userId
                openAndPopUp("$REVERIFICATION_SCREEN/$userId",SPLASH_SCREEN,)

            } else{
                openAndPopUp(LOGIN_SCREEN, SPLASH_SCREEN)

            }
        }
    }



    private fun createAccount(openAndPopUp: (String, String) -> Unit) {
        launchCatching(snackbar = false) {
            try {
                accountService.createAnonymousAccount()
            } catch (ex: FirebaseAuthException) {
                uiState.value = uiState.value.copy(showError = true)
                throw ex
            }
            openAndPopUp(SIGNUP_SCREEN, SPLASH_SCREEN)
        }
    }
}
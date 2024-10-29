package com.hopeinyang.zeroknowledge

import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarManager
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarMessage.Companion.toSnackBarMessage
import com.hopeinyang.zeroknowledge.data.dao.AccountService

import com.hopeinyang.zeroknowledge.data.dao.LogService
import com.hopeinyang.zeroknowledge.data.dao.StorageService
import com.hopeinyang.zeroknowledge.data.dao.UserPreferenceService

import com.hopeinyang.zeroknowledge.data.dto.UserInfo
import com.hopeinyang.zeroknowledge.data.dto.UserPreference
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted


import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class MainViewModel @Inject constructor(
    private val logService: LogService,
    private val accountService: AccountService,
    private val storageService: StorageService,
    private val dataStore:UserPreferenceService



    ): ViewModel() {

    private var _state = MutableStateFlow(UserPreference())
    val mainState: StateFlow<UserPreference>
        get() = _state



        init {

            launchCatching {
                val userId = accountService.currentUserId
                storageService.getUserInfoFlow(userId).collect{user->
                   /* val dept = token["department"] as String
                    val role = token["role"] as String
                    val isAdmin = token["admin"] as Boolean*/

                    val dept = user.data?.department ?: ""
                    val role = user.data?.specialty ?:  ""
                    val isAdmin = user.data?.adminUser ?: false

                    dataStore.updateDepartment(dept)
                    dataStore.updateRole(role)
                    dataStore.updateAdminUser(isAdmin)

                  /*  if (pref.department != dept) dataStore.updateDepartment(dept)
                    if (pref.specialty != role) dataStore.updateRole(role)
                    if (pref.isAdmin != isAdmin) dataStore.updateAdminUser(isAdmin)*/
                }

            }

            launchCatching {

                dataStore.getUserPreferences().collect{pref->
                    _state.value = _state.value.copy(
                        multifactorStatus = pref.multifactorStatus,
                        userEmail = pref.userEmail,
                        userPassword = pref.userPassword,
                        isVerified = pref.isVerified

                    )
                }

            }
        }


    fun launchCatching(snackbar:Boolean = false, onError:(Throwable?)->Unit = {}, block: suspend CoroutineScope.() -> Unit)=
        viewModelScope.launch (
            CoroutineExceptionHandler { _, throwable ->
                if (snackbar){
                    onError(throwable)
                    SnackBarManager.showMessage(throwable.toSnackBarMessage())
                }
                logService.logNonFatalCrash(throwable)

            },
            block = block
        )

    fun reloadUserPreferences(
        currentUser: UserInfo,
        from:String,
        encryptedPassword:String?,
        openAndPopUp:(String, String)-> Unit
    ){


        launchCatching {
            //val userPreference = dataStore.getUserPreferences()
            //val userPref = UserPreference()

            if (encryptedPassword != null) {
                dataStore.updatePassword(encryptedPassword)

            }


            if (!_state.value.isVerified && currentUser.isEmailVerified){

//                userPref.apply {
//                    this.isVerified = true
//                }
                dataStore.updateIsVerified(true)

            }

            if (_state.value.userEmail.isEmpty() && currentUser.email.isNotEmpty()){

                dataStore.updateEmail( currentUser.email)

            }

            if (_state.value.multifactorStatus =="Disabled" && currentUser.secondAuth == currentUser.userId){

                dataStore.updateMultifactorStatus(currentUser.secondAuth)

            }



            //Timber.e("EncryptedPassword is $encryptedPassword")

            openAndPopUp("$HOME_SCREEN/${currentUser.userId}", from)
        }


    }


}
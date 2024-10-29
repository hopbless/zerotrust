package com.hopeinyang.zeroknowledge.ui.screens.HomeScreen

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.type.LatLng
import com.hopeinyang.zeroknowledge.ADD_PATIENT_SCREEN
import com.hopeinyang.zeroknowledge.DETAIL_SCREEN
import com.hopeinyang.zeroknowledge.GUIDELINE_SCREEN
import com.hopeinyang.zeroknowledge.HOME_SCREEN
import com.hopeinyang.zeroknowledge.MANAGE_USER_SCREEN
import com.hopeinyang.zeroknowledge.MainViewModel
import com.hopeinyang.zeroknowledge.PATIENTS_LOG_SCREEN

import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.SETTINGS_SCREEN
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarManager
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarMessage

import com.hopeinyang.zeroknowledge.data.dao.AccountService
import com.hopeinyang.zeroknowledge.data.dao.EncryptionService
import com.hopeinyang.zeroknowledge.data.dao.LocationService

import com.hopeinyang.zeroknowledge.data.dao.LogService
import com.hopeinyang.zeroknowledge.data.dao.StorageService
import com.hopeinyang.zeroknowledge.data.dao.TrustScoreService
import com.hopeinyang.zeroknowledge.data.dao.UserPreferenceService
import com.hopeinyang.zeroknowledge.data.dao.impl.BiometricManagerPrompt
import com.hopeinyang.zeroknowledge.data.dto.Response
import com.hopeinyang.zeroknowledge.data.dto.Status

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime

import javax.inject.Inject


@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    logService: LogService,
    private val accountService: AccountService,
    private val storageService: StorageService,
    private val dataStore:UserPreferenceService,
    private val encryptionService: EncryptionService,
    private val locationService: LocationService,
    private val trustScoreService: TrustScoreService,
    savedStateHandle: SavedStateHandle

):MainViewModel(logService, accountService, storageService , dataStore) {


    private val userId = checkNotNull(savedStateHandle
        .get<String>("userId")){
        throw IllegalArgumentException("userId is required")
    }

    private var _state = MutableStateFlow(HomeScreenUiState())
    val state: StateFlow<HomeScreenUiState>
        get() = _state

    private val department
        get() = _state.value.department

    private val isFingerprintEnabled
        get() = _state.value.isFingerprintEnabled

    private val isPinModeEnable
        get() = _state.value.isPinModeEnable

    private val destination
        get() = _state.value.destination

    private val trustScore
        get() = _state.value.trustScore

    private val role
        get() = _state.value.role

    private val isAdmin
        get() = _state.value.isAdminUser



    init {

            launchCatching (snackbar = true) {

                locationService.getUserCurrentLocation().collect { location ->

                    if (location.status == Status.ERROR) {

                        val hospitalPosition = Pair(
                            _state.value.hospitalLocation.get("latitude")?.toDouble()
                                ?: 0.0,
                            _state.value.hospitalLocation.get("longitude")?.toDouble()
                                ?: 0.0
                        )


                        val userPosition = Pair(38.8977, 77.0365)
                        val mfaEnabled = _state.value.secondAuthFactorEnable
                        val trustScore = trustScoreService.getTrustScore(
                            userPosition,
                            hospitalPosition,
                            mfaEnabled
                        )

                        _state.update {
                            it.copy(
                                isGPSEnabled = false,
                                trustScore = trustScore
                            )
                        }

                       // Timber.e("user Location error ${location.message}")
                    } else if (location.status == Status.SUCCESS) {
                        //Timber.e("hos Location  ${_state.value.hospitalLocation.get("latitude")}")

                        val hospitalPosition = Pair(
                            _state.value.hospitalLocation.get("latitude")?.toDouble()
                                ?: 0.0,
                            _state.value.hospitalLocation.get("longitude")?.toDouble()
                                ?: 0.0
                        )


                        val latitude = location.data?.latitude ?: 0.0
                        val longitude = location.data?.longitude ?: 0.0
                        val userPosition = Pair(latitude, longitude)
                        val mfaEnabled = _state.value.secondAuthFactorEnable

                        val trustScore = trustScoreService.getTrustScore(
                            userPosition,
                            hospitalPosition,
                            mfaEnabled
                        )


                        _state.update {
                            it.copy(
                                trustScore = trustScore,
                                isGPSEnabled = true,
                                userLocation = userPosition
                            )


                        }
                    }


                }
        }


        launchCatching (snackbar = true){
            val user = accountService.currentUser

            val userFlow = storageService.getUserInfoFlow(user.userId)
            val userPref = dataStore.getUserPreferences()
            val homeContentFlow = storageService.getHomePagerContents()
            val hosLocation = storageService.getHospitalLocation()


            combine(
                userFlow,
                userPref,
                homeContentFlow,
                hosLocation


                ) { userInfo, pref, homeContents, loc, ->


                HomeScreenUiState(
                    isEmailVerified = pref.isVerified,
                    emailAddress = pref.userEmail,
                    homeScreenContent = homeContents.data,
                    showViewPager = !homeContents.data.isNullOrEmpty(),
                    secondAuthFactorEnable = pref.multifactorStatus == userId || userId == user.secondAuth,
                    phoneNumber = userInfo.data?.phoneNumber ?: "",
                    isFingerprintEnabled = pref.fingerPrintEnabled,
                    isPinModeEnable = pref.pinModeEnabled,
                    encryptedPin = pref.userPin,
                    firstName = userInfo.data?.firstName ?: "",
                    lastName = userInfo.data?.lastName ?: "",
                    imageUrl = userInfo.data?.imageUrl ?: "",
                    hospitalLocation = loc.data?.gpsCoordinates ?: hashMapOf(),
                    department = pref.department,
                    role = pref.specialty.replaceFirstChar { it.uppercase() },
                    isAdminUser = pref.isAdmin

                    )

            }.collect { _state.value = it }


                }

    }



    fun navigateToSettings(
        option:String,
      naviagteTo:(String)->Unit
    ){
        //navigateAndPop(SETTINGS_SCREEN, HOME_SCREEN)
        when (option){
            "Settings"->{
                naviagteTo(SETTINGS_SCREEN,)

            }
        }


    }

    fun sendEmailVerification(email:String) {
        _state.update {
            it.copy(
                showIndicator = true
            )
        }
        if (email.isNotEmpty()){
            launchCatching (snackbar = true){
                val response = accountService.sendEmailVerification(email)
                if (response.data!!){

                    SnackBarManager.showMessage(R.string.email_verify_success)
                    _state.update {
                        it.copy(
                            showIndicator = false
                        )
                    }
                }

            }
        }else{
            SnackBarManager.showMessage(R.string.email_error)
            _state.update {
                it.copy(
                    showIndicator = false
                )
            }
            return
        }

    }

    fun refreshUser() {
        if (!_state.value.isEmailVerified){
            launchCatching {
                val response = accountService.reloadUser()
                if (response.data!!){
                    Timber.d("User successfully refreshed")
                    val verifiedUser = accountService.currentUser
                    _state.update {

                        it.copy(
                            emailAddress = verifiedUser.email,
                            isEmailVerified = verifiedUser.isEmailVerified
                        )
                    }
                    dataStore.updateIsVerified(verifiedUser.isEmailVerified)
                    val userId = accountService.currentUserId
                    val userUpdate = mapOf(
                        "isEmailVerified" to  verifiedUser.isEmailVerified
                    )
                    storageService.updateUserRecord(userId, userUpdate){}

                }
            }
        }

        val hosLoc = Pair(
            _state.value.hospitalLocation["latitude"]?.toDouble()
                ?: 0.1,
            _state.value.hospitalLocation["longitude"]?.toDouble()
                ?: 1.2
        )
        val userLoc = Pair(38.8977, 77.0365)
        val mfaEnabled = _state.value.secondAuthFactorEnable
        val trustScore = trustScoreService.getTrustScore(
            userLoc, hosLoc,
            mfaEnabled
        )

        _state.update {
            it.copy(
                trustScore = trustScore
            )
        }

    }



    fun onPageCardClick(
        cardTitle: String,
        promptManager: BiometricManagerPrompt,
        openAndNavigate: (String,) -> Unit

        ) {


        onShowModalSheetChange(false)

            when(authType(isPinModeEnable, isFingerprintEnabled)){
                AuthMethod.PIN ->{

                    when( cardTitle){
                        CardName.READ_DOC.title ->{

                            if (trustScore>= 4){
                                updateDestination(DETAIL_SCREEN)
                                openAndNavigate("$DETAIL_SCREEN/$userId")
                            }else{
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("You cannot access this feature at the moment"))
                            }


                        }
                        CardName.ADD_PATIENT.title ->{
                            if (trustScore >= 7 && department.isNotEmpty()){
                                onShowModalSheetChange(true)
                                updateDestination("$ADD_PATIENT_SCREEN/$userId")
                            }else{
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("You cannot access this feature at the moment"))
                            }
                        }

                        CardName.READ_GUIDELINES.title ->{
                            if (trustScore >= 5 && department.isNotEmpty()) {
                                updateDestination("$GUIDELINE_SCREEN/$userId/$role")
                                openAndNavigate(destination)
                            }else{
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("You cannot access this feature at the moment"))
                            }

                        }

                        CardName.BOOKINGS.title->{
                            if (trustScore >= 7) {
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("This feature is not yet available"))
                               /* onShowModalSheetChange(true)
                                updateDestination("$DETAIL_SCREEN/$userId")*/
                            }else{
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("You cannot access this feature at the moment"))
                            }

                        }
                        CardName.VIEW_PATIENTS.title->{

                            if (trustScore >= 7 && department.isNotEmpty()) {
                                onShowModalSheetChange(true)
                                updateDestination("$PATIENTS_LOG_SCREEN/$userId/$department")
                            }else{
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("You cannot access this feature at the moment"))
                            }

                        }
                        CardName.CREATE_ROSTERS.title->{
                            if (trustScore >= 7 && department.isNotEmpty()) {
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("This feature is not yet available"))
                                //onShowModalSheetChange(true)
                                //updateDestination("$PATIENTS_LOG_SCREEN/$userId")
                            }else{
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("You cannot access this feature at the moment"))
                            }
                        }
                        CardName.VIEW_SCHEDULE.title->{

                            if (trustScore >= 7 && department.isNotEmpty()) {
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("This feature is not yet available"))
                                //onShowModalSheetChange(true)
                                //updateDestination("$PATIENTS_LOG_SCREEN/$userId")
                            }else{
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("You cannot access this feature at the moment"))
                            }


                        }

                        CardName.MANAGE_USERS.title ->{
                            if (trustScore >= 7 ){

                                if (isAdmin || role == "Md"){
                                    updateDestination("$MANAGE_USER_SCREEN/$userId")
                                    onShowModalSheetChange(true)
                                }else{
                                    SnackBarManager.showMessage(SnackBarMessage
                                        .StringSnackbar("You do not have the required privileges to access this feature"))
                                }

                            }else{
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("You cannot access this feature at the moment due to low trust score"))
                            }
                        }
                        else -> Unit
                    }

                    //promptManager.showBiometricPrompt("Authenticate", "")
                }
                AuthMethod.BIOMETRIC ->{

                    when(cardTitle){
                        CardName.READ_DOC.title ->{
                            //Timber.e("was here")
                            if (trustScore>= 4){
                                updateDestination("$DETAIL_SCREEN/$userId")
                                openAndNavigate("$DETAIL_SCREEN/$userId")
                            }else{
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("You cannot access this feature at the moment"))
                            }


                        }
                        CardName.ADD_PATIENT.title -> {
                            if (trustScore >= 7 && department.isNotEmpty()){
                                updateDestination("$ADD_PATIENT_SCREEN/$userId")
                                promptManager.showBiometricPrompt("Authenticate", "")
                            }else{
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("You cannot access this feature at the moment"))
                            }
                        }

                        CardName.READ_GUIDELINES.title ->{
                            if (trustScore >= 5 && department.isNotEmpty()){
                                updateDestination("$GUIDELINE_SCREEN/$userId/$role")
                                openAndNavigate(destination)
                            }else{ SnackBarManager.showMessage(SnackBarMessage
                                .StringSnackbar("You cannot access this feature at the moment"))}

                        }
                        CardName.BOOKINGS.title -> {
                            if (trustScore >= 7 && department.isNotEmpty()){
                                //updateDestination("$DETAIL_SCREEN/$userId")
                               // promptManager.showBiometricPrompt("Authenticate", "")
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("This feature is not yet available"))
                            }else{
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("You cannot access this feature at the moment"))
                            }
                        }
                        CardName.VIEW_PATIENTS.title ->{
                            if (trustScore >= 7 && department.isNotEmpty()){
                                updateDestination("$PATIENTS_LOG_SCREEN/$userId/$department")
                                promptManager.showBiometricPrompt("Authenticate", "")
                            }else{
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("You cannot access this feature at the moment"))
                            }
                        }
                        CardName.CREATE_ROSTERS.title ->{
                            if (trustScore >= 7 && department.isNotEmpty()){
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("This feature is not yet available"))
                                //updateDestination("$PATIENTS_LOG_SCREEN/$userId")
                                //promptManager.showBiometricPrompt("Authenticate", "")
                            }else{
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("You cannot access this feature at the moment"))
                            }
                        }

                        CardName.VIEW_SCHEDULE.title ->{
                            if (trustScore >= 7 && department.isNotEmpty()){
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("This feature is not yet available"))

                            }else{
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("You cannot access this feature at the moment"))
                            }
                        }

                        CardName.MANAGE_USERS.title ->{
                            if (trustScore >= 7){
                                Timber.e("role is $role and admin is $isAdmin")
                                if (isAdmin || role == "Md"){
                                    updateDestination("$MANAGE_USER_SCREEN/$userId")
                                    promptManager.showBiometricPrompt("Authenticate", "")
                                }else{
                                    SnackBarManager.showMessage(SnackBarMessage
                                        .StringSnackbar("You do not have the required privileges to access this feature"))
                                }
                            }else{
                                SnackBarManager.showMessage(SnackBarMessage
                                    .StringSnackbar("You cannot access this feature at the moment"))
                            }
                        }

                    }

                }

                AuthMethod.NONE ->{
                    SnackBarManager
                        .showMessage(SnackBarMessage
                            .StringSnackbar("Ensure to add either biometric or create pin for reverification"))
                }

            }

    }


    fun onShowModalSheetChange(show: Boolean) {
        _state.update {
            it.copy(
                showModalSheet = show,
                pinText = ""
            )
        }
    }


    fun onShowViewPager(show: Boolean) {
        _state.value = _state.value.copy(
            showViewPager = show
        )
    }

    fun onPinChange(pin: String) {
        _state.update {
            it.copy(
                pinText = pin
            )
        }
    }

    fun onPinEntered(pin: String, navigateTo: (String) -> Unit) {
       onShowModalSheetChange(false)
        launchCatching (snackbar = true) {
            val encryptedPin = _state.value.encryptedPin
            if (encryptedPin.isNotEmpty()){
                val decryptedPin = requireNotNull( encryptionService.decryptText(encryptedPin)){throw IllegalArgumentException("Unable to verify pin")}

                if (decryptedPin.contentEquals(pin)){
                    //Timber.d("Decrypted pin is: $decryptedPin")
                    navigateTo(destination)
                }else{
                    SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Invalid pin"))
                    return@launchCatching
                }
            }


        }
    }

    fun updateBiometricStatus(navigateTo:(String)->Unit) {
        navigateTo(destination)

    }


    private fun updateDestination(destination: String) {
        _state.update {
            it.copy(destination = destination)
        }
    }




    private fun authType(pinMode:Boolean, fingerprintMode:Boolean):AuthMethod {
            return  when{

                pinMode && fingerprintMode ->{
                    AuthMethod.BIOMETRIC

                }
                pinMode && !fingerprintMode ->{
                    AuthMethod.PIN
                }
                !pinMode && fingerprintMode ->{
                    AuthMethod.BIOMETRIC
                }
                else ->{

                    AuthMethod.NONE
                }


            }
    }

    fun requestGPS(context: Context) {
        val hosLoc = Pair(
            _state.value.hospitalLocation["latitude"]?.toDouble()
                ?: 0.1,
            _state.value.hospitalLocation["longitude"]?.toDouble()
                ?: 1.2
        )
        val userLoc = _state.value.userLocation
        val mfaEnabled = _state.value.secondAuthFactorEnable
        val trustScore = trustScoreService.getTrustScore(
            userLoc, hosLoc,
            mfaEnabled
        )

        _state.update {
            it.copy(
                trustScore = trustScore
            )
        }
        locationService.enabledLocationRequest(context)
    }

    fun cancelIndicator(showIndicator: Boolean) {
       _state.update {
           it.copy(
               showIndicator = showIndicator
           )
       }
    }


    enum class AuthMethod{
        PIN,
        BIOMETRIC,
        NONE

    }


    enum class CardName (val title:String){
        READ_DOC("Read Doc"),
        ADD_PATIENT("Add Patient"),
        READ_GUIDELINES ("Read Guidelines"),
        BOOKINGS ("Bookings"),
        VIEW_PATIENTS("View Patients"),
        CREATE_ROSTERS("Create Roster"),
        VIEW_SCHEDULE("View Schedule"),
        MANAGE_USERS("Manage Users")


    }

}
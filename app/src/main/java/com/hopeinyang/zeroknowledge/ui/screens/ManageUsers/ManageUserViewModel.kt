package com.hopeinyang.zeroknowledge.ui.screens.ManageUsers

import androidx.lifecycle.SavedStateHandle
import com.hopeinyang.zeroknowledge.HOME_SCREEN
import com.hopeinyang.zeroknowledge.MainViewModel
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarManager
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarMessage
import com.hopeinyang.zeroknowledge.data.dao.AccountService
import com.hopeinyang.zeroknowledge.data.dao.LogService
import com.hopeinyang.zeroknowledge.data.dao.StorageService
import com.hopeinyang.zeroknowledge.data.dao.UserPreferenceService
import com.hopeinyang.zeroknowledge.data.dto.HospitalInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ManageUserViewModel @Inject constructor(
    logService: LogService,
    private val storageService: StorageService,
    private val dataStore: UserPreferenceService,
    private val accountService: AccountService,
    savedStateHandle: SavedStateHandle
): MainViewModel(logService, accountService, storageService, dataStore){


    private var _state = MutableStateFlow(ManageUserUiState())
    val state: StateFlow<ManageUserUiState>
        get() = _state


    private val userAccessLevel
        get() = _state.value.accessLevel

    private val userEmail
        get() = _state.value.selectedUserEmail

    private val department
        get() = _state.value.department

    private val role
        get() = _state.value.role

    private val adminUser
        get() = _state.value.isAdminUser

    init {
        val userId = checkNotNull(savedStateHandle.get<String>("userId")){
            throw IllegalArgumentException("userId must be provided")
        }

        val prefFlow = dataStore.getUserPreferences()
        val allUsersFlow = storageService.getAllUsers()
        val allDepartments = storageService.getDepartmentList()
        val hospitalLocationFlow = storageService.getHospitalLocation()

        launchCatching {

            combine(prefFlow,
                allUsersFlow,
                allDepartments,
                hospitalLocationFlow
            ){pref, users, depts, hospice->

                ManageUserUiState(
                    userId = userId,
                    userList = users.data ?: emptyList(),
                    departmentList = depts.data ?: emptyList(),
                    isAdmin = pref.isAdmin,
                    latitude = hospice.data?.gpsCoordinates?.get("latitude") ?: "",
                    longitude = hospice.data?.gpsCoordinates?.get("longitude") ?: "",

                )

            }.collect{_state.value = it}
        }

    }


    fun onBackButtonClick(navigateBack: (String)->Unit) {
        navigateBack("$HOME_SCREEN/${_state.value.userId}")
    }

    fun onUserEmailSelected(email: String) {
        _state.update { state ->
            state.copy(
                selectedUserEmail = email,
                isUserEmailExpanded = false,
                isAdminUser = state.userList.firstOrNull { it.email == email }?.adminUser ?: false
            )

        }
    }

    fun onUserEmailExpandedChanged(isExpanded: Boolean) {
        _state.update {
            it.copy(isUserEmailExpanded = isExpanded)
        }
    }

    fun onDeptExpandedChanged(isExpanded: Boolean) {
        _state.update {
            it.copy(isDeptExpanded = isExpanded)

        }
    }

    fun onSelectedDept(dept: String) {
        _state.update {
            it.copy(
                department = dept,
                isDeptExpanded = false
            )

        }
    }

    fun onRoleExpandedChanged(expanded: Boolean) {
        _state.update {
            it.copy(isRoleExpanded = expanded)

        }
    }

    fun onSelectedRole(role: String) {

        _state.update {
            it.copy(
                role = role,
                isRoleExpanded = false
            )

        }
    }

    fun onAccessLevelChanged(accessLevel: String) {
        if (accessLevel.isEmpty() || accessLevel.isBlank() || accessLevel.toIntOrNull() == null) {
            _state.update {
                it.copy(
                    accessLevel = 0
                )

            }
        }

        try {
            _state.update {
                it.copy(
                    accessLevel = accessLevel.toInt()
                )

            }
        }catch (e: NumberFormatException){
            Timber.e("Number format exception: ${e.message}")
        }


    }

    fun onUpgradeUserButtonClick(clearAndNavigate: (String) -> Unit) {
        if (userEmail.isEmpty()){
            SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Please select a user"))
            return
        }

        if (department.isEmpty()){
            SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Please select a department"))
            return
        }

        if (role.isEmpty()){
            SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Please select user specialty"))
            return
        }

        if (userAccessLevel < 1 || userAccessLevel > 10){
           SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Please enter a valid access level"))
            return
        }

        val userProfile = mapOf(
            "department" to department,
            "role" to role.lowercase(),
            "accessLevel" to userAccessLevel,
            "email" to userEmail,
            "admin" to adminUser
        )
        launchCatching (snackbar = true) {
            accountService.updateUserProfile("addRoleAndDept",userProfile)
        }


    }

    fun onAdminSwitchClick(isAdminUser:Boolean,) {
        if (userEmail.isEmpty()){
            SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Please select a user"))
            return
        }

        _state.update {
            it.copy(
                isAdminUser = isAdminUser
            )

        }
    }

    fun onHospiceLocationChange() {
        if ( _state.value.latitude.isEmpty() || _state.value.longitude.isEmpty()) return
        val location = hashMapOf(
            "latitude" to _state.value.latitude,
            "longitude" to _state.value.longitude
        )

        val hospitalInfo = HospitalInfo(location)
        launchCatching {
            storageService.updateHospiceLocation(hospitalInfo){succeed->
                if (succeed){
                    SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Location updated successfully"))
                }else{
                    SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Failed to update location"))
                }

            }
        }

    }

    fun onLatitudeChanged(latitude: String) {

        _state.update {
            it.copy(
                latitude = latitude
            )
        }
    }

    fun onLongitudeChanged(longitude: String) {
        _state.update {
            it.copy(
                longitude = longitude
            )
        }

    }

}
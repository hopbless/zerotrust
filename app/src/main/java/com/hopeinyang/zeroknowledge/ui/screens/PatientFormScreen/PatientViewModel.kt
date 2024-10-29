package com.hopeinyang.zeroknowledge.ui.screens.PatientFormScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import com.hopeinyang.zeroknowledge.HOME_SCREEN
import com.hopeinyang.zeroknowledge.MainViewModel
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.common.ext.isValidAddress
import com.hopeinyang.zeroknowledge.common.ext.isValidAge
import com.hopeinyang.zeroknowledge.common.ext.isValidBloodSugar
import com.hopeinyang.zeroknowledge.common.ext.isValidBloodType
import com.hopeinyang.zeroknowledge.common.ext.isValidBodyTemp
import com.hopeinyang.zeroknowledge.common.ext.isValidDate
import com.hopeinyang.zeroknowledge.common.ext.isValidDepartment
import com.hopeinyang.zeroknowledge.common.ext.isValidDiagnosis
import com.hopeinyang.zeroknowledge.common.ext.isValidDiastolic
import com.hopeinyang.zeroknowledge.common.ext.isValidFirstName
import com.hopeinyang.zeroknowledge.common.ext.isValidGender
import com.hopeinyang.zeroknowledge.common.ext.isValidHeight
import com.hopeinyang.zeroknowledge.common.ext.isValidLastName
import com.hopeinyang.zeroknowledge.common.ext.isValidPhoneNumber
import com.hopeinyang.zeroknowledge.common.ext.isValidSystolic
import com.hopeinyang.zeroknowledge.common.ext.isValidWeight
import com.hopeinyang.zeroknowledge.common.ext.toUnixTimeStamp
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarManager
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarMessage
import com.hopeinyang.zeroknowledge.data.dao.AccountService
import com.hopeinyang.zeroknowledge.data.dao.LogService
import com.hopeinyang.zeroknowledge.data.dao.StorageService
import com.hopeinyang.zeroknowledge.data.dao.UserPreferenceService
import com.hopeinyang.zeroknowledge.data.dto.PatientInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PatientViewModel @Inject constructor(
    logService: LogService,
    private val storageService: StorageService,
    private val accountService: AccountService,
    private val dataStore: UserPreferenceService,
    savedStateHandle: SavedStateHandle

    ):MainViewModel(logService,  accountService, storageService , dataStore) {

    var state = mutableStateOf(PatientUIState())
        private set




    init {
        val userId = checkNotNull(savedStateHandle.get<String>("userId")){throw IllegalArgumentException("userId must be provided")}
        launchCatching(snackbar = true) {
            storageService.getDepartmentList().collect{
                state.value = state.value.copy(
                    departmentList = it.data ?: emptyList(),
                    userId = userId
                )
            }

        }


    }
    private val firstName
        get() = state.value.firstName

    private val lastName
        get() = state.value.lastName

    private val address
        get() = state.value.address

    private val phoneNumber
        get() = state.value.phoneNumber

    private val dateOfAdmission
        get() = state.value.dateOfAdmission

    private val age
        get() = state.value.age

    private val gender
        get() = state.value.gender

    private val height
        get() = state.value.height

    private val bloodType
        get() = state.value.bloodType

    private val weight
        get() = state.value.weight

    private val bloodPressure
        get() = state.value.bloodPressure

    private val bloodSugar
        get() = state.value.bloodSugar

    private val bodyTemp
        get() = state.value.bodyTemp

    private val systolicBP
        get() = state.value.systolicBP

    private val diastolicBP
        get() = state.value.diastolicBP

    private val diagnosis
        get() = state.value.diagnosis

    private val department
        get() = state.value.department


    fun onFirstNameChange(newValue: String) {
        state.value = state.value.copy(firstName = newValue)
    }

    fun onLastNameChange(newValue: String) {
        state.value = state.value.copy(lastName = newValue)
    }

    fun onAddressChange(newValue: String) {
        state.value = state.value.copy(address = newValue)
    }

    fun onPhoneNumberChange(newValue: String) {
        state.value = state.value.copy(phoneNumber = newValue)
    }

    fun onDateOfAdmissionChange(newValue: String) {
        Timber.e("selected date is $newValue")
        state.value = state.value.copy(dateOfAdmission = newValue)
    }

    fun onAgeChange(newValue: String) {
        state.value = state.value.copy(age = newValue)
    }

    fun onGenderChange(newValue: String) {
        state.value = state.value.copy(gender = newValue)
    }

    fun onHeightChange(newValue: String) {
        state.value = state.value.copy(height = newValue)
    }

    fun onBloodTypeChange(newValue: String) {
        state.value = state.value.copy(bloodType = newValue)

    }

    fun onWeightChange(newValue: String) {
        state.value = state.value.copy(weight = newValue)

    }


    fun onBloodSugarChange(newValue: String) {
        state.value = state.value.copy(bloodSugar = newValue)
    }

    fun onBodyTempChange(newValue: String) {
        state.value = state.value.copy(bodyTemp = newValue)

    }

    fun onDiagnosisChange(newValue: String) {
        state.value = state.value.copy(diagnosis = newValue)
    }

    fun onSystolicChange(systolic: String) {
        state.value = state.value.copy(systolicBP = systolic)

    }

    fun onDiastolicChange(diastolic: String) {
        state.value = state.value.copy(diastolicBP = diastolic)

    }




    fun onSubmitButtonClick(clearAndNavigate:(String)->Unit) {


        if (!firstName.isValidFirstName()) {
            SnackBarManager.showMessage(R.string.first_name_error)
            return
        }

        if (!lastName.isValidLastName()) {
            SnackBarManager.showMessage(R.string.last_name_error)
            return
        }

        if (!address.isValidAddress()) {
            SnackBarManager.showMessage(R.string.address_error)
            return
        }

        if (!age.isValidAge()) {
            SnackBarManager.showMessage(R.string.age_error)
            return
        }

        if (phoneNumber.isEmpty() || phoneNumber.length < 11) {
            SnackBarManager.showMessage(R.string.phone_number_error)
            return
        }


        if (!dateOfAdmission.isValidDate()) {
            SnackBarManager.showMessage(R.string.date_error)
            return
        }

        if (!gender.isValidGender()) {
            SnackBarManager.showMessage(R.string.gender_error)
            return
        }

        if (!weight.isValidWeight()) {
            SnackBarManager.showMessage(R.string.weight_error)
            return
        }

        if (!height.isValidHeight()) {
            SnackBarManager.showMessage(R.string.height_error)
            return
        }

        if (!bloodType.isValidBloodType()) {
            SnackBarManager.showMessage(R.string.blood_type_error)
            return
        }
        if (!systolicBP.isValidSystolic()){
            SnackBarManager.showMessage(R.string.systolic_bp_error)
            return

        }

        if (!diastolicBP.isValidDiastolic()){
            SnackBarManager.showMessage(R.string.diastolic_bp_error)
            return

        }

        if (!bloodSugar.isValidBloodSugar()){
            SnackBarManager.showMessage(R.string.blood_sugar_error)
            return
        }

        if (!bodyTemp.isValidBodyTemp()){
            SnackBarManager.showMessage(R.string.body_temp_error)
            return

        }

        if (!diagnosis.isValidDiagnosis()){
            SnackBarManager.showMessage(R.string.diagnosis_error)
            return
        }

        if (!department.isValidDepartment(state.value.departmentList)){
            SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Select a department"))
            return
        }


        launchCatching (snackbar = true) {
            val patientRecord = PatientInfo(
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                address = address,
                dateOfAdmission = dateOfAdmission.toUnixTimeStamp(),
                lastDateOfVitals = dateOfAdmission.toUnixTimeStamp(),
                nextDateOfAppointment = dateOfAdmission.toUnixTimeStamp(),
                age = age,
                gender = gender,
                height = height,
                bloodType = bloodType,
                vitals = hashMapOf(
                    "systolic" to systolicBP,
                    "diastolic" to diastolicBP,
                    "bloodSugar" to bloodSugar,
                    "bodyTemp" to bodyTemp,
                    "weight" to weight
                ),
                diagnosis = diagnosis,
                department = state.value.department,
            )
            storageService.addNewPatient(patientRecord){task->
                if (task){
                    SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Success>>>"))
                    clearAndNavigate("$HOME_SCREEN/${state.value.userId}")
                }else{
                    SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Something went wrong, try again"))
                }

            }
        }

    }

    fun onDropDownExpandedChanged(isExpanded: Boolean) {
        state.value = state.value.copy(
            isExpanded = isExpanded

        )
    }

    fun onDepartmentChange(department: String) {
        state.value = state.value.copy(
            department = department,
            isExpanded = false

        )
    }


}
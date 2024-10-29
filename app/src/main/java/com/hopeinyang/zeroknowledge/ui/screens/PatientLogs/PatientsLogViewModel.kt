package com.hopeinyang.zeroknowledge.ui.screens.PatientLogs

import androidx.lifecycle.SavedStateHandle
import com.hopeinyang.zeroknowledge.MainViewModel
import com.hopeinyang.zeroknowledge.common.ext.toUnixTimeStamp
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarManager
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarMessage
import com.hopeinyang.zeroknowledge.data.dao.AccountService
import com.hopeinyang.zeroknowledge.data.dao.LogService
import com.hopeinyang.zeroknowledge.data.dao.StorageService
import com.hopeinyang.zeroknowledge.data.dao.UserPreferenceService
import com.hopeinyang.zeroknowledge.data.dto.NewTreatment
import com.hopeinyang.zeroknowledge.data.dto.PatientInfo
import com.hopeinyang.zeroknowledge.data.dto.Status
import com.hopeinyang.zeroknowledge.data.dto.UserInfo
import com.hopeinyang.zeroknowledge.utils.getCurrentDateTimeStamp
import com.hopeinyang.zeroknowledge.utils.getLocalDateFromTimeStamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.time.LocalDate

import javax.inject.Inject

@HiltViewModel
class PatientsLogViewModel @Inject constructor(
    logService: LogService,
    private val storageService: StorageService,
    private val accountService: AccountService,
    private val dataStore: UserPreferenceService,
    savedStateHandle: SavedStateHandle

    ) : MainViewModel(logService, accountService, storageService , dataStore){



    private var _state = MutableStateFlow(PatientsLogUIState())
        val state: StateFlow<PatientsLogUIState>
    get() = _state

    private val patientList
        get() = _state.value.patientList


    init {

        val userId = checkNotNull(savedStateHandle.get<String>("userId")){
            throw IllegalArgumentException("userId is required")}
        val userDepartment = checkNotNull(savedStateHandle.get<String>("department")){
            throw IllegalArgumentException("department is required")}
        launchCatching (snackbar = true) {

            val currentUserFlow = storageService.getUserInfoFlow(userId)
            val currentDateUnixUTC = getCurrentDateTimeStamp(LocalDate.now())
            val patientListFlow = storageService.getAllPatient(currentDateUnixUTC, userDepartment)
            combine(patientListFlow, currentUserFlow,) { patientList, user,  ->
                val patients = if (patientList.status == Status.SUCCESS) patientList.data as List<PatientInfo> else emptyList()
                val userInfo = if (user.status==Status.SUCCESS) user.data as UserInfo else UserInfo()


                PatientsLogUIState(
                    patientList= patients,
                    currentUser = userInfo,
                    userId = userId,
                    userDepartment = userDepartment
                )



            }.collect{_state.value = it}

        }

    }


    fun onQueryChange(query: String) {
       _state.value = _state.value.copy(
           query = query
       )

        launchCatching {
            storageService.getPatient(
                query,
                _state.value.userDepartment
            ) { response ->

                if (response.status == Status.SUCCESS) {
                    val patients = response.data as List<PatientInfo>
                    patients.forEach {
                        Timber.e("Search name log ${it.firstName}")
                    }

                    _state.value = _state.value.copy(
                        patientList = patients
                    )

                }

            }
        }



    }

    fun onSearchClick(searchText: String) {

        _state.update {
            it.copy(
                searchText = searchText,
                isSearchActive = false
            )
        }


    }

    fun onActiveChange(isSearchActive: Boolean) {
        _state.update {
            it.copy(
                isSearchActive = isSearchActive
            )
        }

    }

    fun onClearIconClick(searchText: String) {

        _state.update {
            it.copy(
                query = searchText,
                isSearchActive = false
            )
        }

    }

    fun onSearchHistoryClick(history: String) {

        _state.update {
            it.copy(
                query = history,
                isSearchActive = false
            )
        }

    }

    fun onPatientSelected(selected: PatientInfo) {
        val convertedDate = getLocalDateFromTimeStamp(selected.dateOfAdmission)
        val patient = selected.copy(
            dateOfAdmission = convertedDate,
            firstName = selected.firstName.replaceFirstChar { it.uppercase() },
            lastName = selected.lastName.replaceFirstChar { it.uppercase() }

        )

        _state.update {
            it.copy(
                searchPatient = patient,
                isSearchActive = false
            )
        }


    }

    fun onNewTreatmentButtonClick() {

        _state.update {
            it.copy(
                newTreatmentClick = true
            )
        }

    }

    fun onLabTestChange(tests: String) {
        _state.update {
            it.copy(
                labTest = tests
            )
        }

    }

    fun onPrescriptionChange(prescriptions: String) {

        _state.update {
            it.copy(
                prescription = prescriptions
            )
        }

    }

    fun onDoctorCommentsChange(comments: String) {

        _state.update {
            it.copy(
                doctorComments = comments
            )
        }

    }

    fun onCancelTreatmentButtonClick() {

        _state.update {
            it.copy(
                newTreatmentClick = false
            )
        }


    }

    fun onSaveTreatmentButtonClick() {
        if (_state.value.currentUser.userId.isNotEmpty()){
            launchCatching (snackbar = true) {
                val newTreatment = NewTreatment(
                    patientId = _state.value.selectedPatient.patientId,
                    labTests = _state.value.labTest,
                    prescription = _state.value.prescription,
                    doctorComments = _state.value.doctorComments,
                    nextAppointment = _state.value.dateOfNextAppointment.toUnixTimeStamp(),
                    treatedBy = _state.value.currentUser.firstName + " " + _state.value.currentUser.lastName,
                    treatment = _state.value.treatment,
                    treatmentDate = getCurrentDateTimeStamp(LocalDate.now())
                )
                storageService.addTreatment( newTreatment){isSuccessful->
                    if (isSuccessful) {

                        SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("New treatment added successfully"))

                        _state.update {
                            it.copy(
                                newTreatmentClick = false
                            )
                        }

                    }else SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Unable to add new treatment, make you are a doctor"))
                }

            }

        }else   Timber.e("current user is null")

    }

    fun onNextAppointmentChange(dateString: String) {

        _state.update {
            it.copy(
                dateOfNextAppointment = dateString
            )
        }

    }

    fun onNewTreatmentChange(treatment: String) {

        _state.update {
            it.copy(
                treatment = treatment
            )
        }

    }

    fun onExpandedChange(isExpanded: Boolean) {
        _state.update {
            it.copy(
                isExpanded = isExpanded
            )
        }
    }

    fun onSelectedPatient(fullName:String){
        val firstName = fullName.substringBefore(" ")
        val lastName = fullName.substringAfter(" ")
        val selectedPatient = patientList.first { it.firstName ==firstName && it.lastName == lastName  }
        val selected = selectedPatient.copy(
            dateOfAdmission = getLocalDateFromTimeStamp(selectedPatient.dateOfAdmission),
            firstName = selectedPatient.firstName.replaceFirstChar { it.uppercase() },
            lastName = selectedPatient.lastName.replaceFirstChar { it.uppercase() }
        )
        _state.update {
            it.copy(
                selectedPatient = selected,
                isExpanded = false,

            )
        }

        launchCatching (snackbar = true) {
            storageService.getAllPatientTreatments(selectedPatient.patientId){isSuccessful, treatmentList->
                if (isSuccessful && treatmentList.isNotEmpty()) {
                    val updatedTreatmentList = treatmentList.map {
                        it.copy(
                            treatmentDate = getLocalDateFromTimeStamp(it.treatmentDate)
                        )
                    }

                    _state.update {
                        it.copy(
                            treatmentList = updatedTreatmentList,
                            lastTreatment = treatmentList.last()
                        )
                    }

                }
            }


        }

    }

    fun onTreatmentExpanded(treatmentExpanded: Boolean) {
        _state.update {
            it.copy(
                treatmentIsExpanded = treatmentExpanded
            )
        }
    }

    fun onTreatmentDateSelected(selectedTreatmentDate: String) {

        val selectedTreatment = _state.value.treatmentList.first { it.treatmentDate == selectedTreatmentDate }
        _state.update {
            it.copy(
                lastTreatment = selectedTreatment,
                treatmentIsExpanded = false,
                selectedTreatmentDate = selectedTreatmentDate
            )
        }
    }
}
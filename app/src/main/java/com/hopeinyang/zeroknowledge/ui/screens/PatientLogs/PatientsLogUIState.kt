package com.hopeinyang.zeroknowledge.ui.screens.PatientLogs

import com.hopeinyang.zeroknowledge.data.dto.NewTreatment
import com.hopeinyang.zeroknowledge.data.dto.PatientInfo
import com.hopeinyang.zeroknowledge.data.dto.UserInfo

data class PatientsLogUIState(

    val patientList:List<PatientInfo> = emptyList(),
    val query:String="",
    val searchText:String="",
    val isSearchActive:Boolean = false,
    val searchPatient:PatientInfo = PatientInfo(),
    val newTreatmentClick:Boolean = false,
    val labTest:String = "",
    val prescription:String="",
    val doctorComments:String="",
    val dateOfNextAppointment:String = "",
    val currentUser: UserInfo = UserInfo(),
    val treatedBy:String = "",
    val treatment:String ="",
    val userId:String ="",
    val userDepartment:String = "",
    val isExpanded:Boolean = false,
    val selectedPatient:PatientInfo = PatientInfo(),
    val treatmentList:List<NewTreatment> = emptyList(),
    val lastTreatment:NewTreatment = NewTreatment(),
    val treatmentIsExpanded:Boolean = false,
    val selectedTreatmentDate:String = ""
)

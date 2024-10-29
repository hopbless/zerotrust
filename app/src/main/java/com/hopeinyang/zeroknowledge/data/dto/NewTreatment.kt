package com.hopeinyang.zeroknowledge.data.dto

data class NewTreatment(
    val labTests: String = "",
    val prescription: String = "",
    val doctorComments: String = "",
    val nextAppointment: String = "",
    val treatedBy: String = "",
    val patientId: String = "",
    val treatment:String="",
    val treatmentDate: String=""
)

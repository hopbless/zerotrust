package com.hopeinyang.zeroknowledge.data.dto

import com.google.firebase.firestore.DocumentSnapshot

data class PatientInfo(
    val firstName: String ="",
    val lastName: String ="",
    val phoneNumber: String ="",
    val address: String ="",
    val dateOfAdmission: String ="",
    val age: String ="",
    val gender: String ="",
    val height: String ="",
    val bloodType: String ="",
    val vitals:HashMap<String, String> = hashMapOf(),
    val diagnosis: String ="",
    val lastTreatments: String ="",
    val prescription: String= "",
    val doctorComments: String ="",
    val lastDateOfAppointment: String ="",
    val nextDateOfAppointment: String ="",
    val patientId: String ="",
    val lastDateOfVitals: String ="",
    val department: String ="",
) {

}

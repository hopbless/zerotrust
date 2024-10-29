package com.hopeinyang.zeroknowledge.ui.screens.PatientFormScreen

data class PatientUIState(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val dateOfAdmission: String ="",
    val age: String ="",
    val gender: String ="",
    val height: String ="",
    val weight: String ="",
    val bloodType: String ="",
    val bloodPressure: String ="",
    val bodyTemp: String ="",
    val bloodSugar: String ="",
    val systolicBP:String ="",
    val diastolicBP:String ="",
    val vitals:HashMap<String, String> = hashMapOf(),
    val diagnosis: String ="",
    val isCorrectEntries: Boolean = false,
    val userId:String = "",
    val departmentList:List<String> = listOf(),
    val department:String = "",
    val isExpanded:Boolean = false,


)

package com.hopeinyang.zeroknowledge.common.ext


import android.util.Patterns
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.regex.Pattern


private const val MIN_PASS_LENGTH = 6
private const val PASS_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$"

fun String.isValidEmail(): Boolean {
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return this.isNotBlank() &&
            this.length >= MIN_PASS_LENGTH &&
            Pattern.compile(PASS_PATTERN).matcher(this).matches()
}

fun String.passwordMatches(repeated: String): Boolean {
    return this == repeated
}

fun String.isValidAge(): Boolean{
    return this.isNotBlank() && this.toInt() > 5
}

fun String.isValidPhoneNumber(): Boolean{
    this.trim().filterNot { it.isWhitespace() }
    return  this.isNotBlank() &&
            this.length >= 10 &&
            this[0] != '0'
}

fun String.isValidPhoneCode():Boolean{
    return this.isNotBlank() && this.length > 1 && this[0] == '+'
}

fun String.isValidHeight(): Boolean{
    return this.isNotBlank() &&
            this.toFloat() <= 3.0 &&
            this.toFloat() >= 1.0
}

fun String.isValidFirstName(): Boolean{
    this.trim().filterNot { it.isWhitespace() }
    return  this.isNotBlank() &&
            this.length > 2
}

fun String.isValidLastName(): Boolean{
    this.trim().filterNot { it.isWhitespace() }
    return  this.isNotBlank() &&
            this.length > 2
}

fun String.isValidAddress(): Boolean{
    this.trim().filterNot { it.isWhitespace() }
    return  this.isNotBlank() &&
            this.length > 2
}

fun String.isValidHospitalName(): Boolean{
    return  this.length > 3 && this.isNotBlank()
}

fun String.isValidDate(): Boolean{
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return (LocalDate.parse(this, dateTimeFormatter))!=null && this.isNotBlank()
}

fun String.isValidGender():Boolean{
    return this.isNotBlank() && (this.lowercase() == "male" || this.lowercase() == "female")
}

fun String.isValidWeight():Boolean{
    return this.isNotBlank() && this.toFloat() > 10.0 && this.toFloat() < 200.0
}

fun String.isValidBloodType():Boolean{
    return this.isNotBlank() && this.length > 1
}

fun String.isValidSystolic():Boolean{
    return this.isNotBlank() && this.toInt() < 400 && this.toInt() > 10

}

fun String.isValidDiastolic():Boolean{
    return this.isNotBlank() && this.toInt() < 200 && this.toInt() > 10

}

fun String.isValidBloodSugar(): Boolean{
    return this.isNotBlank() &&
            this.toFloat() <= 15.0 &&
            this.toFloat() >= 1.0
}

fun String.isValidBodyTemp(): Boolean{
    return this.isNotBlank() &&
            this.toFloat() <= 50.0 &&
            this.toFloat() >= 10.0
}

fun String.isValidDiagnosis():Boolean{
    return this.isNotBlank() && this.length > 3
}

fun String.isValidDepartment(departmentList:List<String>):Boolean{
    return this.isNotBlank() && this in departmentList
}





fun String.toDate(): LocalDate {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    return LocalDate.parse(this, dateTimeFormatter)
}

fun String.toTime(): LocalTime {
    val dateTimeFormatter = DateTimeFormatterBuilder()
        .parseCaseInsensitive().appendPattern("hh:mm a").toFormatter()
    return LocalTime.parse(this, dateTimeFormatter)

}

fun String.toUnixTimeStamp():String{
    val longDate = LocalDate.parse(this, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    return longDate.atStartOfDay(ZoneId.of("UTC")).toInstant().epochSecond.toString()
}

fun String.isPinValid(pin: String): Boolean {
    return this == pin && this.isNotBlank() && this.length == 4
}

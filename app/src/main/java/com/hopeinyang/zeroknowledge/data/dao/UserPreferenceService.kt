package com.hopeinyang.zeroknowledge.data.dao

import com.hopeinyang.zeroknowledge.data.dto.UserPreference
import kotlinx.coroutines.flow.Flow

interface UserPreferenceService {


    fun getUserPreferences(): Flow<UserPreference>


    suspend fun updatePassword(password: String)

    suspend fun  updateEmail(email: String)

    suspend fun updatePin(pin: String)


    suspend fun updateIsVerified(isVerified:Boolean)

    suspend fun updateMultifactorStatus(status:String)
    suspend fun updatePinMode(pinMode:Boolean)

    suspend fun updateFingerPrint(fingerPrint:Boolean)
    suspend fun updateDepartment(department:String)
    suspend fun updateRole(role:String)
    suspend fun updateAdminUser(admin:Boolean)
}
package com.hopeinyang.zeroknowledge.data.dao.impl

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hopeinyang.zeroknowledge.data.dao.AccountService
import com.hopeinyang.zeroknowledge.data.dao.StorageService
import com.hopeinyang.zeroknowledge.data.dao.UserPreferenceService
import com.hopeinyang.zeroknowledge.data.dto.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferenceRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,


):UserPreferenceService{



    private object PreferencesKeys {
        val MULTIFACTOR_STATUS = stringPreferencesKey("multifactor_status")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PASSWORD = stringPreferencesKey("user_password")
        val USER_PIN = stringPreferencesKey("user_pin")
        val PIN_MODE_ENABLED = booleanPreferencesKey("pin_mode_enabled")
        val FINGERPRINT_MODE_ENABLED = booleanPreferencesKey("fingerprint_mode_enabled")
        val IS_USER_VERIFIED = booleanPreferencesKey("is_user_verified")
        val USER_DEPARTMENT = stringPreferencesKey("user_department")
        val USER_ROLE = stringPreferencesKey("user_role")
        val ADMIN_USER = booleanPreferencesKey("admin_user")
    }


    override fun getUserPreferences(): Flow<UserPreference> = dataStore.data
        .catch { exception ->
            if (exception is IOException){
                emit(emptyPreferences())

            }else{
                throw exception
            }
        }

        .map {
                preferences ->
            UserPreference(
                multifactorStatus = preferences[PreferencesKeys.MULTIFACTOR_STATUS] ?: "Disabled",
                userEmail = preferences[PreferencesKeys.USER_EMAIL] ?: "",
                isVerified = preferences[PreferencesKeys.IS_USER_VERIFIED] ?: false,
                pinModeEnabled = preferences[PreferencesKeys.PIN_MODE_ENABLED] ?: false,
                userPin = preferences[PreferencesKeys.USER_PIN] ?: "",
                fingerPrintEnabled = preferences[PreferencesKeys.FINGERPRINT_MODE_ENABLED] ?: false,
                userPassword = preferences[PreferencesKeys.USER_PASSWORD] ?: "",
                department = preferences[PreferencesKeys.USER_DEPARTMENT] ?: "",
                specialty = preferences[PreferencesKeys.USER_ROLE] ?: "",
                isAdmin = preferences[PreferencesKeys.ADMIN_USER] ?: false


            )
        }


    override suspend fun updatePassword(password: String) {
        dataStore.edit {
                preferences ->
            preferences[PreferencesKeys.USER_PASSWORD] = password
        }
    }

    override suspend fun updateEmail(email: String) {
        dataStore.edit{
                preferences ->
            preferences[PreferencesKeys.USER_EMAIL] = email
        }
    }

    override suspend fun updatePin(pin: String) {
        dataStore.edit {
                preferences ->
            preferences[PreferencesKeys.USER_PIN] = pin
        }
    }



    override suspend fun updateIsVerified(isVerified: Boolean) {
        dataStore.edit {
                preferences ->
            preferences[PreferencesKeys.IS_USER_VERIFIED] = isVerified
        }
    }

    override suspend fun updateMultifactorStatus(status: String) {
        dataStore.edit {
                preferences ->
            preferences[PreferencesKeys.MULTIFACTOR_STATUS] = status
        }
    }

    override suspend fun updatePinMode(pinMode: Boolean) {
        dataStore.edit {
                preferences ->
            preferences[PreferencesKeys.PIN_MODE_ENABLED] = pinMode
        }
    }

    override suspend fun updateFingerPrint(fingerPrint: Boolean) {
        dataStore.edit {
                preferences ->
            preferences[PreferencesKeys.FINGERPRINT_MODE_ENABLED] = fingerPrint
        }
    }

    override suspend fun updateDepartment(department: String) {
        dataStore.edit {
            preferences ->
            preferences[PreferencesKeys.USER_DEPARTMENT] = department
        }
    }

    override suspend fun updateRole(role: String) {
        dataStore.edit {
                preferences ->
            preferences[PreferencesKeys.USER_ROLE] = role
        }
    }

    override suspend fun updateAdminUser(admin: Boolean) {
        dataStore.edit {
                preferences ->
            preferences[PreferencesKeys.ADMIN_USER] = admin
        }
    }
}
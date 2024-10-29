package com.hopeinyang.zeroknowledge.data.dao

import android.content.Context
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.MultiFactorResolver
import com.hopeinyang.zeroknowledge.data.dto.OtpAuthResult
import com.hopeinyang.zeroknowledge.data.dto.Response
import com.hopeinyang.zeroknowledge.data.dto.UserInfo
import kotlinx.coroutines.flow.Flow

interface AccountService {


    val currentUserId: String
    val hasUser: Boolean

    val currentUser: UserInfo

    val storeService:StorageService


    fun userToken(): Flow<Map<String, Any>>
    suspend fun authenticate(
        context: Context,
        email: String,
        password: String,
        navigateTo:(String, OtpAuthResult)-> Unit,
        onSuccessLogin: (Boolean, String) -> Unit
    )


    suspend fun reloadUser():Response<Boolean?>

    suspend fun sendSMS(
        context: Context,
        phoneNumber:String,
        from: String,
        onRequestSent:(Boolean, String, OtpAuthResult?)->Unit
    )
    suspend fun verifySMSCode(
        smsId: String,
        otpValue: String,
        isSuccessful: (Boolean) -> Unit
    )
    suspend fun authenticateSecondFactor(
        verificationId: String,
        smsCode: String?,
        multiFactorResolver: MultiFactorResolver,
        onSuccessLogin:(Boolean)->Unit
    )
    suspend fun sendRecoveryEmail(email: String)
    suspend fun sendEmailVerification(email: String):Response<Boolean?>
    suspend fun createAnonymousAccount()
    suspend fun createUserWithEmailAndPassword(
        mail: String,
        password: String,
        onSuccessSignup: (Boolean) -> Unit
    )

    suspend fun updateUserProfile(func:String, userProfile: Map<String, Any>)
    suspend fun linkAccount(email: String, password: String)
    suspend fun deleteAccount()
    suspend fun signOut()
    fun getAuthState():Flow<Response<Boolean?>>
}
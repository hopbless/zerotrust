package com.hopeinyang.zeroknowledge.data.dao.impl

import android.content.Context
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApiNotAvailableException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMultiFactorException
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.MultiFactorAssertion
import com.google.firebase.auth.MultiFactorResolver
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.auth.PhoneMultiFactorGenerator
import com.google.firebase.auth.PhoneMultiFactorInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.hopeinyang.zeroknowledge.HOME_SCREEN
import com.hopeinyang.zeroknowledge.LOGIN_SCREEN
import com.hopeinyang.zeroknowledge.OTP_SCREEN
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.SETTINGS_SCREEN
import com.hopeinyang.zeroknowledge.common.ext.getActivity
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarManager
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarMessage
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarMessage.Companion.toSnackBarMessage
import com.hopeinyang.zeroknowledge.data.dto.OtpAuthResult
import com.hopeinyang.zeroknowledge.data.dto.Response
import com.hopeinyang.zeroknowledge.data.dto.UserInfo
import com.hopeinyang.zeroknowledge.data.dao.AccountService
import com.hopeinyang.zeroknowledge.data.dao.StorageService
import com.hopeinyang.zeroknowledge.data.dao.UserPreferenceService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val userPreferenceService: UserPreferenceService,
    private val storageService: StorageService,
    private val functions: FirebaseFunctions



): AccountService {



    private var multiFactorResolver: MultiFactorResolver? = null

    override val storeService
        get() = this.storageService


    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()



    override val hasUser: Boolean
        get() = auth.currentUser != null

    override val currentUser: UserInfo
        get() = auth.currentUser?.let {

            (if (it.multiFactor.enrolledFactors.size > 0) it.multiFactor.enrolledFactors[0].displayName else "Disabled")?.let { disp ->
                //Timber.d("User Display Name is $disp")
                UserInfo(
                    email = it.email ?: "",
                    isEmailVerified = it.isEmailVerified,
                    phoneNumber = it.phoneNumber ?: "",
                    secondAuth = disp,
                    userId = it.uid,
                )
            }
        } ?: UserInfo()



    override fun userToken(): Flow<Map<String, Any>> = callbackFlow{
        val listener = FirebaseAuth.IdTokenListener { auth->
            auth.currentUser?.getIdToken(false)?.addOnSuccessListener {
                val dept = it.claims["dept"] ?: ""
                val role = it.claims["role"]  ?: ""
                val isAdmin = it.claims["admin"] ?: false
                Timber.e("admim is: $isAdmin and dept is $dept and role is $role")
                val claim = mapOf(
                    "department" to dept,
                    "role" to role,
                    "admin" to isAdmin
                )
                this.trySend(claim)

            }
        }
        auth.addIdTokenListener(listener)
        awaitClose { auth.removeIdTokenListener(listener) }
    }



    override suspend fun authenticate(
        context: Context,
        email: String,
        password: String,
        navigateTo:(String, OtpAuthResult)-> Unit,
        onSuccessLogin: (Boolean, String) -> Unit
    ) {


        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                OnCompleteListener { task ->

                    if (task.isSuccessful) {
                        // User is not enrolled with a second factor and is successfully
                        // signed in.
                        //
                        //Timber.d("Verification completed")
                        onSuccessLogin(true, "")
                        return@OnCompleteListener
                    }
                    if (task.exception is FirebaseAuthMultiFactorException) {


                        val callbacks = object : OnVerificationStateChangedCallbacks() {
                            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                                //Timber.d("Verification complete")
                                throw RuntimeException(
                                    "onVerificationCompleted() triggered with instant-validation and auto-retrieval disabled.",
                                )

                            }

                            override fun onVerificationFailed(e: FirebaseException) {
                                if (e is FirebaseAuthInvalidCredentialsException) {

                                    onSuccessLogin(false, e.toString())
                                    // Invalid


                                    // ...
                                } else if (e is FirebaseTooManyRequestsException) {
                                    // The SMS quota for the project has been exceeded
                                    // ...

                                    onSuccessLogin(false, e.toString())

                                }
                            }

                            override fun onCodeSent(
                                verificationId: String,
                                token: PhoneAuthProvider.ForceResendingToken
                            ) {
                                val authResult = OtpAuthResult(
                                    smsId = verificationId,
                                    multiFactorResolver = multiFactorResolver,
                                    from = LOGIN_SCREEN,
                                    resendingToken = token,
                                    password = password
                                )

                                //Timber.d("Pass: $password")


                                navigateTo(OTP_SCREEN, authResult)
                                //Timber.d("SMS Code Send with Id: $verificationId")
                            }
                        }

                        // The user is a multi-factor user. Second factor challenge is
                        // required.
                         multiFactorResolver =
                            (task.exception as FirebaseAuthMultiFactorException).resolver
                            val selectedHint = multiFactorResolver!!.hints[0] as PhoneMultiFactorInfo
                            context.getActivity()?.let {activity->
                                PhoneAuthProvider.verifyPhoneNumber(
                                    PhoneAuthOptions.newBuilder()
                                        .setActivity(activity)
                                        .setMultiFactorHint(selectedHint)
                                        .setTimeout(30L, TimeUnit.SECONDS)
                                        .setMultiFactorSession(multiFactorResolver!!.session)
                                        .setCallbacks(callbacks)
                                        .build()
                                )

                            }



                        // ...
                    } else {
                        onSuccessLogin(false, task.exception.toString())
                        task.exception?.let { SnackBarManager.showMessage(it.toSnackBarMessage()) }
                    }
                })


//        val user = auth.signInWithEmailAndPassword(email, password).await().user
//        return user?.uid.orEmpty()

    }

    override suspend fun reloadUser(): Response<Boolean?> {
        return try {
            auth.currentUser?.reload()?.await()
                Response.success(true)
        }catch (e:Exception){
            Response.error(e.toString(), false)
        }
    }


    override suspend fun sendSMS(
        context: Context,
        phoneNumber: String,
        from:String,
        onRequestSent: (Boolean, String, OtpAuthResult?) -> Unit
        )

    {
        //Timber.d("SMS Request")

        //var smsVerificationId = ""
        val callbacks = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                Timber.d("Verification complete")
                throw RuntimeException(
                    "onVerificationCompleted() triggered with instant-validation and auto-retrieval disabled.",
                )

            }

            override fun onVerificationFailed(e: FirebaseException) {

                when(e){
                    is FirebaseAuthInvalidCredentialsException->{
                        onRequestSent(false, e.message.toString(), null)
                    }
                    is FirebaseTooManyRequestsException ->{
                        onRequestSent(false, e.message.toString(), null)
                    }
                    is FirebaseAuthMultiFactorException ->{
                        onRequestSent(false, e.message.toString(), null)
                    }

                    is FirebaseApiNotAvailableException ->{
                        onRequestSent(false, e.message.toString(), null)
                    }
                    else ->{
                        onRequestSent(false, e.message.toString(), null)
                    }
                }

            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {

                val authResult = OtpAuthResult(
                    smsId = verificationId,
                    from = from,
                    isSuccessful = true,
                    phoneNumber = phoneNumber,
                    resendingToken = token

                )
                onRequestSent(true, "SMSCodeSent", authResult)

            }

        }

        auth.currentUser!!
            .multiFactor
            .session
            .addOnCompleteListener { task->
            if (task.isSuccessful){
                val phoneAuthOptions = context.getActivity()?.let { activity ->


                    PhoneAuthOptions.newBuilder()
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(30L, TimeUnit.SECONDS)
                    .setMultiFactorSession(task.result)
                    .setCallbacks(callbacks)
                    .setActivity(activity)
                    .requireSmsValidation(true)
                    .build()

                }
                if (phoneAuthOptions != null){
                    //Timber.d("phoneOptions: $phoneAuthOptions")
                    PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)
                }else{
                    //Timber.d("phoneOptions is null")
                }



            }else onRequestSent(false, task.exception?.message.toString(), null)

        }


    }

    override suspend fun verifySMSCode(
        smsId: String,
        otpValue: String,
        isSuccessful: (Boolean) -> Unit
    ) {
        if(smsId.isNotEmpty()){

            val credential = PhoneAuthProvider.getCredential(smsId, otpValue)
            val multiFactorAssertion = PhoneMultiFactorGenerator.getAssertion(credential)
            auth
                .currentUser
                ?.multiFactor
                ?.enroll(multiFactorAssertion, currentUserId)
                ?.addOnCompleteListener {task->
                    if (task.isSuccessful){
                        SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("SMS verified successfully"))

                        isSuccessful(true)
                    }
                    else{
                        isSuccessful(false)
                    }

                    // ...
                }
        }
    }


    override suspend fun authenticateSecondFactor(
        verificationId: String,
        smsCode: String?,
        multiFactorResolver: MultiFactorResolver,
        onSuccessLogin:(Boolean)->Unit
    ) {
        if (smsCode != null){
        val credential = PhoneAuthProvider.getCredential(verificationId, smsCode)
            val multiFactorAssertion: MultiFactorAssertion =
            PhoneMultiFactorGenerator.getAssertion(credential)
            multiFactorResolver
                .resolveSignIn(multiFactorAssertion)
                .addOnCompleteListener {task->
                    if (task.isSuccessful){
                        onSuccessLogin(true)
                    }else{
                        onSuccessLogin(false)
                    }

                }

        }

    }




    override suspend fun sendRecoveryEmail(email: String) {
        TODO("Not yet implemented")
    }

    override suspend fun sendEmailVerification(email: String): Response<Boolean?> {
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
            Response.success(true)
        }catch (e:Exception){
            Response.error("Error occur", false)
        }

    }

    override suspend fun createAnonymousAccount() {
        TODO("Not yet implemented")
    }

    override suspend fun createUserWithEmailAndPassword(
        userEmail: String,
        password: String,
        onSuccessSignUp: ( Boolean) -> Unit

        ) {


      auth.createUserWithEmailAndPassword(userEmail,password)
          .addOnSuccessListener {

              onSuccessSignUp( true)
              }
          .addOnFailureListener {
              onSuccessSignUp(false)
          }
    }

    override suspend fun updateUserProfile(func:String, userProfile: Map<String, Any>) {

        updateCloudFunction(func, userProfile).addOnCompleteListener { task->
            if (task.isSuccessful){
                Timber.d("Profile Updated")

                SnackBarManager.showMessage(SnackBarMessage.StringSnackbar(task.result))
            }else{
                val e = task.exception
                e?.printStackTrace()
                if (e is FirebaseFunctionsException){
                    val code = e.code
                    Timber.e("Firebase Functions Exception Code: $code")
                    SnackBarManager.showMessage(e.toSnackBarMessage())
                }

            }

        }

    }

    override suspend fun linkAccount(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAccount() {
        auth.currentUser!!.delete().await()
    }

    override suspend fun signOut() {
        if (auth.currentUser!!.isAnonymous) {
            auth.currentUser!!.delete()
        }
        userPreferenceService.updateIsVerified(false)
        userPreferenceService.updateMultifactorStatus("Disabled")
        userPreferenceService.updatePin("")
        userPreferenceService.updateEmail("")
        userPreferenceService.updatePassword("")
        userPreferenceService.updatePinMode(false)
        userPreferenceService.updateFingerPrint(false)
        userPreferenceService.updateRole("")
        userPreferenceService.updateDepartment("")
        userPreferenceService.updateAdminUser(false)
        auth.signOut()
    }

    override fun getAuthState():Flow<Response<Boolean?>> = callbackFlow {
        val authStateListener = AuthStateListener{auth->

            val response = auth.currentUser == null
            trySend(Response.success(response,))
        }
        auth.addAuthStateListener (authStateListener)
        awaitClose {
            auth.removeAuthStateListener (authStateListener)
        }

    }

    private fun updateCloudFunction(func:String, data: Map<String, Any>):Task<String>{
        return functions
            .getHttpsCallable(func)
            .call(data)
            .continueWith { task ->
                val result = task.result?.data.toString()
                result
            }
    }




    companion object {
        private const val KEY_VERIFICATION_ID = "key_verification_id"
        const val EXTRA_MFA_RESOLVER = "EXTRA_MFA_RESOLVER"
    }


}
package com.hopeinyang.zeroknowledge.data.dao.impl

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class BiometricManagerPrompt(
    private val activity:AppCompatActivity,
) {

    private val resultChannel = Channel<BiometricResult>()
    val promptResults = resultChannel.receiveAsFlow()

    fun showBiometricPrompt(
        title:String,
        description:String,
    ){
        val manager = BiometricManager.from(activity)

        val authenticators = if(Build.VERSION.SDK_INT >=30){
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        } else BIOMETRIC_STRONG

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setDescription(description)
            .setAllowedAuthenticators(authenticators)

        if (Build.VERSION.SDK_INT < 30){
            promptInfo.setNegativeButtonText("Cancel")
        }

        when(manager.canAuthenticate(authenticators)){
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->{
                resultChannel.trySend(BiometricResult.HardwareUnavailable)
                //resultChannel.trySend("HardwareUnavailable")
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->{
                resultChannel.trySend(BiometricResult.FeatureUnavailable)
                //resultChannel.trySend("HardwareUnavailable")
                return
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED->{
                resultChannel.trySend(BiometricResult.AuthenticationNotSet)
                //resultChannel.trySend("HardwareUnavailable")
                return
            }
            else -> Unit
        }

        val prompt = BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback(){
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    resultChannel.trySend(BiometricResult.AuthenticationError(errString.toString()))
                    //resultChannel.trySend("HardwareUnavailable")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    resultChannel.trySend(BiometricResult.AuthenticationSuccess)
                    //resultChannel.trySend("HardwareUnavailable")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    resultChannel.trySend(BiometricResult.AuthenticationFailed)
                    // resultChannel.trySend("HardwareUnavailable")
                }
            }
        )

        prompt.authenticate(promptInfo.build())
    }

    sealed interface BiometricResult{
        data object HardwareUnavailable: BiometricResult
        data object FeatureUnavailable:BiometricResult
        data class AuthenticationError(val error:String):BiometricResult
        //data object AuthenticationError:BiometricResult
        data object AuthenticationFailed:BiometricResult
        data object AuthenticationSuccess:BiometricResult
        data object AuthenticationNotSet:BiometricResult
    }
}
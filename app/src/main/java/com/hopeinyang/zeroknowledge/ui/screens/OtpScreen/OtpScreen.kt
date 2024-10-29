package com.hopeinyang.zeroknowledge.ui.screens.OtpScreen


import android.app.Activity
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect

import com.google.android.gms.auth.api.phone.SmsRetriever
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.SETTINGS_SCREEN
import com.hopeinyang.zeroknowledge.common.composables.BasicButton
import com.hopeinyang.zeroknowledge.common.composables.OtpInputField
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarManager
import com.hopeinyang.zeroknowledge.data.dto.OtpAuthResult
import com.hopeinyang.zeroknowledge.data.services.OTPReceiver
import com.hopeinyang.zeroknowledge.data.services.startSMSRetrieverClient
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, )
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun OtpScreen(
    viewModel: OtpViewModel = hiltViewModel(),
    authResult: OtpAuthResult?,
    openAndPopUp:(String, String)->Unit
) {
    val uiState by viewModel.uiState
    val context = LocalContext.current

    var timer by remember { mutableStateOf("" )}

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current


    OtpReceiverEffect(
        context = context,
        onOtpReceived = { otp ->
            viewModel.onOtpValueChange(otp)
            if (uiState.otpValue.length == 6) {
                keyboardController?.hide()
                viewModel.isOtpFilled(true)
            }
        }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    LaunchedEffect( key1 = uiState.isTimerActive) {
        if (uiState.isTimerActive){

            var seconds = 0
            while(seconds < 31){
                timer = if (seconds < 10) "0:0$seconds" else "0:$seconds"
                delay(1000)
                seconds++

            }
            viewModel.resendSMS(context)

            timer = ""
            viewModel.setTimerActive(false)


        }

    }

    LaunchedEffect (Unit) {
        viewModel.updateOtpState(authResult)
    }

    /**
     * Set status bar color for this screen
     */
    (LocalView.current.context as Activity).window.statusBarColor = MaterialTheme.colorScheme.surface.toArgb()

    /**
     * OTP Screen UI starts here
     */
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                    },
                navigationIcon = {
                    Box(
                        Modifier
                            .size(48.dp)
                            .clickable { viewModel.navigateBack(openAndPopUp) }) {

                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = "Back",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
                colors =TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.surface
                ),
                title = { Text(
                    text = "Enter One Time Password",
                    color = MaterialTheme.colorScheme.surface,
                    fontWeight = FontWeight.Bold
                ) },
                windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    viewModel.verifySMSCode(uiState.otpValue, authResult, openAndPopUp)
                },
                enabled = uiState.isOtpFilled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            ) {
                Text(text = "Continue")
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp, 0.dp),
                    text = uiState.otpSubTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(24.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    OtpInputField(
                        modifier = Modifier
                            .padding(top = 48.dp)
                            .focusRequester(focusRequester),
                        otpText = uiState.otpValue,
                        shouldCursorBlink = false,
                        onOtpModified = { value, otpFilled ->
                            viewModel.onOtpValueChange(value)
                            viewModel.isOtpFilled(otpFilled)
                            if (otpFilled) {
                                keyboardController?.hide()
                            }
                        }
                    )


                }
                /*if (false){

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        BasicButton(
                            text = R.string.resend_sms,
                            enableButton = uiState.enableSMSButton,
                            modifier = Modifier
                                .heightIn(15.dp)
                                .widthIn()
                                .padding(end = 20.dp)

                        ) {
                            viewModel.setTimerActive(true)
                            SnackBarManager.showMessage(R.string.sms_sent)

                        }

                        Text(
                            text = timer,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(
                                fontSize = 30.sp,
                                color = MaterialTheme.colorScheme.primary

                            )
                        )

                    }
                }*/

            }
        }
    }

}

//@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun OtpReceiverEffect(
    context: Context,
    onOtpReceived: (String) -> Unit
) {
    val otpReceiver = remember { OTPReceiver() }

    /**
     * This function should not be used to listen for Lifecycle.Event.ON_DESTROY because Compose
     * stops recomposing after receiving a Lifecycle.Event.ON_STOP and will never be aware of an
     * ON_DESTROY to launch onEvent.
     *
     * This function should also not be used to launch tasks in response to callback events by way
     * of storing callback data as a Lifecycle.State in a MutableState. Instead, see currentStateAsState
     * to obtain a State that may be used to launch jobs in response to state changes.
     */
    LifecycleResumeEffect (Unit){
        // add ON_RESUME effect here
        Log.e("OTPReceiverEffect", "SMS retrieval has been started.")
        startSMSRetrieverClient(context)
        otpReceiver.init(object : OTPReceiver.OTPReceiveListener {
            override fun onOTPReceived(otp: String?) {
                Log.e("OTPReceiverEffect ", "OTP Received: $otp")
                otp?.let { onOtpReceived(it) }
                try {
                    Log.e("OTPReceiverEffect ", "Unregistering receiver")
                    context.unregisterReceiver(otpReceiver)
                } catch (e: IllegalArgumentException) {
                    Log.e("OTPReceiverEffect ", "Error in registering receiver: ${e.message}}")
                }
            }

            override fun onOTPTimeOut() {
                Log.e("OTPReceiverEffect ", "Timeout")
            }
        })
        try {
            Log.e("OTPReceiverEffect ", "Lifecycle.Event.ON_RESUME")
            Log.e("OTPReceiverEffect ", "Registering receiver")

            context.registerReceiver(
                otpReceiver,
                IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
                Context.RECEIVER_EXPORTED
            )
        } catch (e: IllegalArgumentException) {
            Log.e("OTPReceiverEffect ", "Error in registering receiver: ${e.message}}")
        }
        onPauseOrDispose {
            // add clean up for work kicked off in the ON_RESUME effect here
            try {
                Log.e("OTPReceiverEffect ", "Lifecycle.Event.ON_PAUSE")
                Log.e("OTPReceiverEffect ", "Unregistering receiver")
                context.unregisterReceiver(otpReceiver)
            } catch (e: IllegalArgumentException) {
                Log.e("OTPReceiverEffect ", "Error in unregistering receiver: ${e.message}}")
            }
        }
    }
}
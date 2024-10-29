package com.hopeinyang.zeroknowledge.ui.screens.ReverificationScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hopeinyang.zeroknowledge.HOME_SCREEN
import com.hopeinyang.zeroknowledge.LOGIN_SCREEN
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.common.composables.ButtonComponent
import com.hopeinyang.zeroknowledge.common.composables.ComposePinInput
import com.hopeinyang.zeroknowledge.common.composables.ComposePinInputStyle
import com.hopeinyang.zeroknowledge.common.composables.HeadingTextComponent
import com.hopeinyang.zeroknowledge.common.composables.NormalTextComponent

import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarManager
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarMessage
import com.hopeinyang.zeroknowledge.data.dao.ConnectivityObserver
import com.hopeinyang.zeroknowledge.data.dao.impl.BiometricManagerPrompt
import com.hopeinyang.zeroknowledge.data.dto.OtpAuthResult
import kotlinx.coroutines.delay
import timber.log.Timber

@Composable
fun ReverificationScreen(
    viewModel: ReverificationViewModel = hiltViewModel(),
    biometricPrompt: BiometricManagerPrompt,
    navigateToOtpScreen:(String, OtpAuthResult?)->Unit,
    clearAndNavigate:(String)->Unit
){

    val context = LocalContext.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val biometricResult by biometricPrompt.promptResults.collectAsState(initial = null)



    val novelUser = !uiState.isUserPinEnabled &&
            !uiState.isFingerprintsEnabled &&
            !uiState.isSecondAuthEnable

    LaunchedEffect(uiState.userId.isNotEmpty()) {
        //Timber.d("userId is ${uiState.userId} and is user novel $novelUser and ${uiState.networkStatus == ConnectivityObserver.InternetStatus.Available.name}")

        delay(3_000L)
        if (novelUser && uiState.userId.isNotEmpty() &&
            uiState.networkStatus == ConnectivityObserver.InternetStatus.Available.name
        ) {

            clearAndNavigate("$HOME_SCREEN/${uiState.userId}")
        }
    }

    LaunchedEffect(biometricResult) {
        if (biometricResult is BiometricManagerPrompt.BiometricResult.AuthenticationSuccess && uiState.userId.isNotEmpty()){
            clearAndNavigate("$HOME_SCREEN/${uiState.userId}")
        }/*else if (biometricResult is BiometricManagerPrompt.BiometricResult.AuthenticationFailed){
            //SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("Invalid Authentication"))
        }
*/
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {


        Surface(
            color = MaterialTheme.colorScheme.background,

            ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp)

            ) {

                NormalTextComponent(value = stringResource(id = R.string.app_name))
                HeadingTextComponent(value = stringResource(id = R.string.welcome_back))
                Spacer(modifier = Modifier.heightIn(80.dp))

                //
                if (uiState.isUserPinEnabled) {
                    Text(
                        text = "Please enter your pin to login",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))



                    Box(contentAlignment = Alignment.Center) {
                        ComposePinInput(
                            value = uiState.pinText,
                            onValueChange = { viewModel.onPinChange(it) },
                            onPinEntered = { viewModel.onPinEntered(it, clearAndNavigate) },
                            fontColor = MaterialTheme.colorScheme.onSurface,
                            cellBorderColor = MaterialTheme.colorScheme.tertiary,
                            focusedCellBorderColor = MaterialTheme.colorScheme.primary,
                            mask = '*',
                            style = ComposePinInputStyle.BOX,
                            modifier = Modifier
                                .align(Alignment.Center)

                        )
                    }
                }
                if(uiState.isFingerprintsEnabled) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Login using fingerprint",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 10.dp)
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(100.dp)
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.fingerprint_24px),
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clickable { biometricPrompt.showBiometricPrompt(
                                    title = "Login",
                                    description = "Securely login to your app"
                                )}



                        )
                    }
                }


                if (uiState.userEncryptedPassword.isNotEmpty() && uiState.secondAuthStatus == uiState.userId){

                    if (!uiState.isUserPinEnabled && !uiState.isFingerprintsEnabled) {
                        Spacer(modifier = Modifier.height(40.dp))
                        ButtonComponent(
                            value = stringResource(id = R.string.login_with_otp),
                        ) {
                            viewModel.onOtpLoginClick(
                                context,
                                clearAndNavigate,
                                navigateToOtpScreen
                            )
                        }
                    }
                }else{
                    if (uiState.userId.isNotEmpty() && uiState.userEncryptedPassword.isEmpty()){
                        clearAndNavigate(LOGIN_SCREEN)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))


            }
        }
    }

}
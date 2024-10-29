package com.hopeinyang.zeroknowledge.ui.screens.HomeScreen

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.common.composables.BasicButton
import com.hopeinyang.zeroknowledge.common.composables.ComposableLifecycle
import com.hopeinyang.zeroknowledge.common.composables.DropdownContextMenu
import com.hopeinyang.zeroknowledge.common.composables.HomeScreenViewPager
import com.hopeinyang.zeroknowledge.common.composables.PinInputModalBottomSheet
import com.hopeinyang.zeroknowledge.common.composables.ZeroTrustUserHeaderCard
import com.hopeinyang.zeroknowledge.common.ext.getActivity
import com.hopeinyang.zeroknowledge.data.dao.impl.BiometricManagerPrompt
import com.hopeinyang.zeroknowledge.data.dao.impl.LocationUpdates
import com.hopeinyang.zeroknowledge.data.dto.Response
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    promptManager:BiometricManagerPrompt,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    navigateTo:(String)->Unit,



){
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState()
    var showDropDown by remember { mutableStateOf(false)}
    val biometricResult by promptManager.promptResults.collectAsState(initial = null)
    val context = LocalContext.current



    val enrollLauncher= rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )

    var showLoading by remember {
        mutableStateOf(false)
    }


    LaunchedEffect (biometricResult) {
        //Timber.e("Setting screen when clicked ${biometricResult.toString()}")
        if (biometricResult is BiometricManagerPrompt.BiometricResult.AuthenticationNotSet){
            if (Build.VERSION.SDK_INT >= 30){
                val enrollment = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                    )
                }
                enrollLauncher.launch(enrollment)
            }
        }else if (biometricResult is BiometricManagerPrompt.BiometricResult.AuthenticationSuccess){
            viewModel.updateBiometricStatus(navigateTo)
        }


    }

    LaunchedEffect (uiState.emailAddress) {
        if (uiState.emailAddress.isEmpty()){
            showLoading = true
            delay(3_000L)
            if (!uiState.isEmailVerified){
                viewModel.refreshUser()
            }
        }else{
            showLoading = false
        }


    }

    LaunchedEffect (uiState.showIndicator, uiState.isGPSEnabled) {
        if (uiState.showIndicator){
            delay(5_000L)
            viewModel.cancelIndicator(false)
        }

        if (!uiState.isGPSEnabled){
            viewModel.requestGPS(context)
        }
    }







    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = { CenterAlignedTopAppBar(
            modifier = Modifier,
            title = {
                Text(
                    text = "Zero Trust",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn()
                        .padding(start = 30.dp),
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                        color = MaterialTheme.colorScheme.onPrimary,
                    ),
                    textAlign = TextAlign.Center

                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                actionIconContentColor = MaterialTheme.colorScheme.surface
            ),
            navigationIcon = {
               /* IconButton(onClick = { }) {

                    Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = null,
                        modifier = Modifier.size(40.dp))

                }*/
            },
            actions = {

               /* IconButton(onClick = {  viewModel.navigateToSettings(navigateAndPop) }) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                }
*/
                    DropdownContextMenu(
                        options = listOf("Settings"),
                        isExpanded = showDropDown,
                        modifier = Modifier.clickable {
                            showDropDown = true
                        },

                        onExpandedChanged = {showDropDown = it},
                    ) { option->
                        showDropDown = false
                        viewModel.navigateToSettings(option, navigateTo)

                    }



            },

            )}

    ){innerPadding->

        ComposableLifecycle {_, event->
            when(event){
                Lifecycle.Event.ON_RESUME->{
                    viewModel.refreshUser()
                }


                else->{

                }
            }

        }


        if (!showLoading) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                if (!uiState.isEmailVerified) {

                    VerificationScreen(
                        showIndicator = uiState.showIndicator
                    ) {

                        viewModel.sendEmailVerification(uiState.emailAddress)
                    }
                }

                if (uiState.showModalSheet) {
                    PinInputModalBottomSheet(
                        pin = uiState.pinText,
                        sheetState = sheetState,
                        onPinEntered = { viewModel.onPinEntered(it, navigateTo) },
                        onDismissRequest = { viewModel.onShowModalSheetChange(false) },
                        onPinChange = { viewModel.onPinChange(it)},
                        modifier = Modifier
                    )
                }

                ZeroTrustUserHeaderCard(
                    userImageUrl = uiState.imageUrl,
                    userFullName = uiState.firstName + " " + uiState.lastName,
                    trustScore = uiState.trustScore.toString(),
                    role = uiState.role,
                    isGPSEnable = uiState.isGPSEnabled,
                    onLocationIconClick = {
                        if (!uiState.isGPSEnabled)
                            viewModel.requestGPS(context)
                    }
                )
                if (uiState.showViewPager) {
                    HomeScreenViewPager(
                        homeScreenContent = uiState.homeScreenContent,
                    ) { cardTitle ->
                        viewModel.onPageCardClick(
                            cardTitle,
                            promptManager,
                            navigateTo
                        )
                    }

                }




            }
        }else{
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }

    }
}

@Composable
private fun VerificationScreen(
    showIndicator:Boolean = false,
    onVerifyClick:()->Unit
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {


        Text(
            text = "Your email is yet to be verified, click below to verify",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Justify,
            modifier = Modifier
                .padding(16.dp)

        )
        BasicButton(
            text = R.string.very_email, enableButton = true, modifier = Modifier
        ) {
            onVerifyClick()
        }

        if (showIndicator){
            Box (contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .alpha(0.5f)
                )
            }
        }

    }
}




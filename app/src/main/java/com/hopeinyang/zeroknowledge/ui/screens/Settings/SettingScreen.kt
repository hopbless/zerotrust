package com.hopeinyang.zeroknowledge.ui.screens.Settings

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.hopeinyang.zeroknowledge.HOME_SCREEN
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.SETTINGS_SCREEN

import com.hopeinyang.zeroknowledge.common.composables.MFACard
import com.hopeinyang.zeroknowledge.common.composables.SettingsItemCard
import com.hopeinyang.zeroknowledge.common.composables.SignOutCard

import com.hopeinyang.zeroknowledge.common.composables.ZeroTrustCenterTopBar
import com.hopeinyang.zeroknowledge.common.ext.card
import com.hopeinyang.zeroknowledge.data.dao.impl.BiometricManagerPrompt
import com.hopeinyang.zeroknowledge.data.dto.OtpAuthResult
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    promptManager: BiometricManagerPrompt,
    navigateToOtpScreen:(String, OtpAuthResult )->Unit,
    restartApp: (String)-> Unit,
    openAndNavigate:(String) -> Unit,
    navigateBack:(String, String)->Unit
){
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val biometricResult by promptManager.promptResults.collectAsState(initial = null)


    val enrollLauncher= rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )
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
            viewModel.updateBiometricStatus()
        }


    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { ZeroTrustCenterTopBar(
            scrollBehavior = scrollBehavior,
            isAdminAccount = false,
            title = R.string.settings,
            titleColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.surface,
            navIconColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.primaryContainer,
            actionIconColor = MaterialTheme.colorScheme.onSurface,
            navigateToSettings = { /*TODO*/ },
            navigateBack = {viewModel.navigateBackToHome(navigateBack)})
        }
    ){innerPadding ->

        Column (

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top

        ) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp,),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceContainer
            )

            Box (
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp)
                    .padding(start = 16.dp, top = 4.dp, bottom = 4.dp)
            ){
                if (uiState.userImageUrl.isNotEmpty()){
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = uiState.userImageUrl,
                            placeholder = painterResource(id = R.drawable.place_holder)
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .align(Alignment.CenterStart)
                            .requiredSize(100.dp)

                    )
                }else{
                    Image(
                        painter =  painterResource(id = R.drawable.place_holder),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .align(Alignment.CenterStart)
                            .requiredSize(100.dp)

                    )
                }

                Text(
                    text = uiState.firstName,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 120.dp, top = 10.dp)
                )

                Text(
                    text = uiState.userEmail,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 120.dp, top = 40.dp)
                )

                Text(
                    text = uiState.department,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 120.dp, top = 70.dp)
                )

            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp,),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceContainer
            )


            SettingsItemCard(
                title = R.string.account,
                imageVector = Icons.Default.AccountCircle ,
                subTitle = R.string.edit_account,
                onEditClick = { viewModel.onEditAccount(openAndNavigate)},
                highlightColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .card()
                    .padding(top = 8.dp)
            )


            MFACard(
                phoneNumber = uiState.phoneNumber,
                highlightColor = MaterialTheme.colorScheme.primary,
                switchCheck = uiState.isMFASwitch,
                label = R.string.phone_number,
                painter = painterResource(id = R.drawable.call_asset),
                isMFASwitchEnabled = uiState.isSecondAuthEnabled,
                onSwitchChange = {viewModel.onMFASwitchChange(it)},
                onTextChange = {viewModel.onPhoneNumberChange(it)},
                modifier = Modifier
                    .card()
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            ){

                viewModel.onEnableMFA(context,it, navigateToOtpScreen)
            }

            SettingsItemCard(
                title = R.string.add_fingerprint,
                imageVector = Icons.Filled.Face ,
                subTitle = R.string.add_biometric,
                onEditClick = { },
                highlightColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .card()
                    .padding(top = 8.dp),
                addSwitch = true,
                switchCheck = uiState.isBiometricEnabled,
                onSwitchChange = {viewModel.onBiometricSwitchChange(it, promptManager)}
            )

            SettingsItemCard(
                title = R.string.add_pin_login,
                imageVector = Icons.Filled.Lock ,
                subTitle = R.string.add_pin,
                onEditClick = { },
                highlightColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .card()
                    .padding(top = 8.dp, bottom = 8.dp),
                addSwitch = true,
                switchCheck = uiState.isPinModeEnabled,
                onSwitchChange = {viewModel.onPinModeEnabledChange(it, openAndNavigate )}
            )
            SignOutCard { viewModel.onSignOutClick(restartApp) }
            //DeleteMyAccountCard { viewModel.onDeleteMyAccountClick(restartApp) }



        }


    }

}
package com.hopeinyang.zeroknowledge

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarManager
import com.hopeinyang.zeroknowledge.data.dao.impl.BiometricManagerPrompt
import com.hopeinyang.zeroknowledge.data.dao.impl.LocationServiceImpl
import com.hopeinyang.zeroknowledge.data.dto.OtpAuthResult
import com.hopeinyang.zeroknowledge.data.dto.TempCardDetails
import com.hopeinyang.zeroknowledge.ui.screens.AddPinScreen.AddPinScreen
import com.hopeinyang.zeroknowledge.ui.screens.DetailScreen.DetailScreen
import com.hopeinyang.zeroknowledge.ui.screens.GuidelineScreen.GuidelineScreen
import com.hopeinyang.zeroknowledge.ui.screens.HomeScreen.HomeScreen
import com.hopeinyang.zeroknowledge.ui.screens.Login.LoginScreen
import com.hopeinyang.zeroknowledge.ui.screens.ManageUsers.ManageUserScreen

import com.hopeinyang.zeroknowledge.ui.screens.OtpScreen.OtpScreen
import com.hopeinyang.zeroknowledge.ui.screens.PatientFormScreen.PatientFormScreen
import com.hopeinyang.zeroknowledge.ui.screens.PatientLogs.PatientsLogScreen
import com.hopeinyang.zeroknowledge.ui.screens.ReverificationScreen.ReverificationScreen
import com.hopeinyang.zeroknowledge.ui.screens.Settings.AccountScreen
import com.hopeinyang.zeroknowledge.ui.screens.Settings.SettingScreen
import com.hopeinyang.zeroknowledge.ui.screens.SignUp.SignupScreen
import com.hopeinyang.zeroknowledge.ui.screens.SplashScreen.SplashScreen
import com.hopeinyang.zeroknowledge.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ZeroKnowledgeApp(
    promptManager:BiometricManagerPrompt,
    viewModel: MainViewModel = hiltViewModel()

){




    AppTheme {
        Surface (color = MaterialTheme.colorScheme.surface) {
            val appState = rememberAppState()


            Scaffold(
                snackbarHost = {
                    SnackbarHost (
                        hostState = appState.snackbarHostState.value,
                        modifier = Modifier.padding(8.dp),
                        snackbar = {snackbarData ->
                            Snackbar(
                                snackbarData,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        })
                }
            ){innerPadding->
                NavHost(navController = appState.navController,
                    startDestination = SPLASH_SCREEN ,
                    modifier = Modifier.padding(innerPadding)
                ){
                    zeroKnowledgeGraph(appState, promptManager)
                }


            }

        }
    }

}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun NavGraphBuilder.zeroKnowledgeGraph(appState: ZeroKnowledgeAppState, promptManager: BiometricManagerPrompt){
    composable(route = SPLASH_SCREEN){
        SplashScreen(
            openAndPopUp = {route, pop -> appState.navigateAndPopUp(route,pop)}
        )

    }

    composable(route = SIGNUP_SCREEN){
        SignupScreen(
            clearAndNavigate = {route, -> appState.clearAndNavigate(route,)},
            navigateTo = {route-> appState.navigatePopBackStack(route)}
        )
    }

    composable(route = LOGIN_SCREEN){


        LoginScreen(
            openAndPopUp = {route, popUp -> appState.navigateAndPopUp(route,popUp)},

            navigateTo = {
                route, authResult->
                appState.navController.currentBackStackEntry?.savedStateHandle?.set("authResult", authResult)
                Timber.d("Is Parcelable null? ${authResult.multiFactorResolver?.hints?.size}")
                appState.navigate(route)
            }
        )
    }

    composable(route = OTP_SCREEN,){

        val param = appState.navController
            .previousBackStackEntry
            ?.savedStateHandle
            ?.get<OtpAuthResult>("authResult")

        OtpScreen (
            authResult = param,
            openAndPopUp = {route, popup -> appState.navigateAndPopUp(route,popup)},

        )
    }

    composable(route = "$HOME_SCREEN/{userId}"){
        HomeScreen(
            promptManager = promptManager,
            navigateTo = {route-> appState.navigate(route)},

        )
    }

    composable(route = SETTINGS_SCREEN){
        SettingScreen(
            promptManager = promptManager,
            navigateToOtpScreen = {
                                  route, otpAuthResult  ->
                appState.navController
                    .currentBackStackEntry
                    ?.savedStateHandle?.set("authResult", otpAuthResult)
                appState.navigate(route)

                                  },
            restartApp = {route-> appState.clearAndNavigate(route)},
            openAndNavigate = {route-> appState.navigate(route)},
            navigateBack = {route, popUp -> appState.navigateAndPopUp(route, popUp)}
        )
    }

    composable(route = "$PATIENTS_LOG_SCREEN/{userId}/{department}"){
        PatientsLogScreen (
            clearAndNavigate = {route-> appState.clearAndNavigate(route)}

        )
    }

    composable("$ADD_PATIENT_SCREEN/{userId}"){


        PatientFormScreen(
            clearAndNavigate = {route-> appState.clearAndNavigate(route)},
        )

    }

    composable(route = ACCOUNT_SCREEN){
        AccountScreen(
            openAndPop = {route, pop -> appState.navigateAndPopUp(route, pop)}

        )

    }



    composable(route = "$DETAIL_SCREEN/{userId}"){

        DetailScreen(
            navigateBack = {
                           route, popUp ->
                            appState.navigateAndPopUp(route, popUp)
            }
        )

    }

    composable(route = "$GUIDELINE_SCREEN/{userId}/{role}"){

        GuidelineScreen(
            navigateBack = {
                    route, popUp ->
                appState.navigateAndPopUp(route, popUp)

            }
        )

    }

    composable(route= "$REVERIFICATION_SCREEN/{userId}"){
        ReverificationScreen(
            biometricPrompt = promptManager ,
            navigateToOtpScreen = { route, authResult ->
                appState.navController
                    .currentBackStackEntry
                    ?.savedStateHandle?.set("authResult", authResult)

                appState.navigate(route)

            }
        ) {
            route ->
            appState.clearAndNavigate(route)

        }
    }

    composable(route = "$ADD_PIN_SCREEN/{userId}"){

        AddPinScreen(
            clearAndNavigate = {route-> appState.clearAndNavigate(route)}
        )

    }

    composable(route = "$MANAGE_USER_SCREEN/{userId}"){
        ManageUserScreen(
            clearAndNavigate = {route-> appState.clearAndNavigate(route)}
        )
    }
}

@Composable
fun rememberAppState(
    snackbarHostState: SnackbarHostState = remember{ SnackbarHostState() },
    navController: NavHostController = rememberNavController(),
    snackBarManager: SnackBarManager = SnackBarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(snackbarHostState, navController, snackBarManager, resources, coroutineScope){
    ZeroKnowledgeAppState(snackbarHostState, navController, snackBarManager, resources, coroutineScope)
}

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}


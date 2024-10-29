package com.hopeinyang.zeroknowledge

import android.content.res.Resources
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarManager
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarMessage.Companion.toMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class ZeroKnowledgeAppState(
    snackbarHostState: SnackbarHostState,
    val navController: NavHostController,
    private val snackbarManager: SnackBarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope
) {

    val snackbarHostState = mutableStateOf(snackbarHostState)

    init{
        coroutineScope.launch {
            snackbarManager.snackbarMessages.filterNotNull().collect{snackBarMessage->
                val text = snackBarMessage.toMessage(resources)
                snackbarHostState.showSnackbar(text)

            }
        }
    }

    fun popUp() {
        navController.popBackStack()
    }

    fun navigate(route: String) {
        navController.navigate(route) { launchSingleTop = true }
    }

    fun navigateAndPopUp(route: String, popUp: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(popUp) { inclusive = true }
        }
    }

    fun clearAndNavigate(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(0) { inclusive = true }
        }
    }

    fun navigatePopBackStack(route: String) {
        navController.popBackStack(route, inclusive = false)

    }

}
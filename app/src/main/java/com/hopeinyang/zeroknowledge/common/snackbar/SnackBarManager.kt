package com.hopeinyang.zeroknowledge.common.snackbar

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SnackBarManager {
    private val messages: MutableStateFlow<SnackBarMessage?> = MutableStateFlow(null)
    val snackbarMessages: StateFlow<SnackBarMessage?>
        get() = messages.asStateFlow()

    fun showMessage(@StringRes message: Int){
        messages.value = SnackBarMessage.ResourceSnackbar(message)
    }

    fun showMessage(message: SnackBarMessage){
        messages.value = message
    }
}
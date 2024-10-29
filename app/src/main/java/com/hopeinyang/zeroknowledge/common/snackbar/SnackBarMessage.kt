package com.hopeinyang.zeroknowledge.common.snackbar

import android.content.res.Resources
import androidx.annotation.StringRes
import com.hopeinyang.zeroknowledge.R


sealed class SnackBarMessage {

    class StringSnackbar(val message: String): SnackBarMessage()
    class ResourceSnackbar(@StringRes val message: Int): SnackBarMessage()

    companion object{
        fun SnackBarMessage.toMessage(resources: Resources):String{
            return when (this){
                is StringSnackbar -> this.message
                is ResourceSnackbar -> resources.getString(this.message)

            }
        }

        fun Throwable.toSnackBarMessage(): SnackBarMessage{
            val message = this.message.orEmpty()
            return if (message.isNotBlank()) StringSnackbar(message)
            else ResourceSnackbar (R.string.generic_error)
        }
    }
}
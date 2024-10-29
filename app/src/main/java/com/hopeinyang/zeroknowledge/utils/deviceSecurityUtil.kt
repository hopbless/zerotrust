package com.hopeinyang.zeroknowledge.utils

import android.app.KeyguardManager
import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.provider.Settings


fun isDeviceScreenLocked(context: Context):Boolean{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        isDeviceLocked(context)
    }else{
        isPatternSet(context) || isPassOrPinSet(context)
    }
}

private fun isDeviceLocked(context: Context): Boolean {
   val keyguardManager:KeyguardManager =
       context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return keyguardManager.isDeviceSecure
}

private fun isPassOrPinSet(context: Context): Boolean {
    val keyguardManager: KeyguardManager =
        context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return keyguardManager.isDeviceSecure

}

private fun isPatternSet(context: Context): Boolean {
    val cr: ContentResolver = context.contentResolver
    return try {
        val lockPattenEnable: Int =
            Settings.Secure.getInt(cr, Settings.Secure.LOCK_PATTERN_ENABLED)
        lockPattenEnable == 1

    }catch (e:Settings.SettingNotFoundException){
        false

    }
}

fun isDeviceADBEnabled(context: Context):Boolean{
    val adb:Int = Settings.Secure.getInt(context.contentResolver, Settings.Global.ADB_ENABLED,0)
    val wirelessADB = Settings.Secure.getInt(context.contentResolver, "adb_wifi_enable",0)
    return  adb == 1 || wirelessADB != 0
}



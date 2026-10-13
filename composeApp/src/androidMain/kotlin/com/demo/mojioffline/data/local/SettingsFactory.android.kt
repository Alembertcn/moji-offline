package com.demo.mojioffline.data.local

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.Settings

private lateinit var appContext: Context

fun initAndroidSettings(context: Context) {
    appContext = context.applicationContext
}

actual fun createSettings(): Settings {
    check(::appContext.isInitialized) { "Call initAndroidSettings() before createSettings()" }
    return SharedPreferencesSettings(
        appContext.getSharedPreferences("moji_offline", Context.MODE_PRIVATE),
    )
}

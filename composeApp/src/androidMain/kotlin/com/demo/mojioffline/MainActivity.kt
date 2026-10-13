package com.demo.mojioffline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.demo.mojioffline.core.di.appModule
import com.demo.mojioffline.data.local.initAndroidSettings
import org.koin.compose.KoinApplication

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initAndroidSettings(applicationContext)
        setContent {
            KoinApplication(application = {
                modules(appModule)
            }) {
                App()
            }
        }
    }
}

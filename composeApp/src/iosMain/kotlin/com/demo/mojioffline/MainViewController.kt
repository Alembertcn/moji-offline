package com.demo.mojioffline

import androidx.compose.ui.window.ComposeUIViewController
import com.demo.mojioffline.core.di.appModule
import org.koin.compose.KoinApplication
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    KoinApplication(application = {
        modules(appModule)
    }) {
        App()
    }
}

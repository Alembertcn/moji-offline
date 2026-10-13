package com.demo.mojioffline

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.demo.mojioffline.domain.model.AppPreferences
import com.demo.mojioffline.domain.repository.PreferencesRepository
import com.demo.mojioffline.feature.history.HistoryRoute
import com.demo.mojioffline.feature.settings.SettingsRoute
import com.demo.mojioffline.ui.i18n.LocalAppStrings
import com.demo.mojioffline.ui.i18n.ProvideAppLocale
import com.demo.mojioffline.ui.theme.MojiTheme
import org.koin.compose.koinInject

private object Routes {
    const val History = "history"
    const val Settings = "settings"
}

@Composable
fun App(
    preferencesRepository: PreferencesRepository = koinInject(),
) {
    val preferences by preferencesRepository.observePreferences().collectAsStateWithLifecycle(
        initialValue = AppPreferences(),
    )

    ProvideAppLocale(preferences.language) {
        MojiTheme(themeMode = preferences.themeMode) {
            val strings = LocalAppStrings.current
            val navController = rememberNavController()
            val backStack by navController.currentBackStackEntryAsState()
            val current = backStack?.destination?.route ?: Routes.History

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = current == Routes.History,
                            onClick = {
                                navController.navigate(Routes.History) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Text("H") },
                            label = { Text(strings.tabHistory) },
                        )
                        NavigationBarItem(
                            selected = current == Routes.Settings,
                            onClick = {
                                navController.navigate(Routes.Settings) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Text("S") },
                            label = { Text(strings.tabSettings) },
                        )
                    }
                },
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = Routes.History,
                    modifier = Modifier.padding(padding),
                ) {
                    composable(Routes.History) {
                        HistoryRoute(language = preferences.language)
                    }
                    composable(Routes.Settings) {
                        SettingsRoute()
                    }
                }
            }
        }
    }
}

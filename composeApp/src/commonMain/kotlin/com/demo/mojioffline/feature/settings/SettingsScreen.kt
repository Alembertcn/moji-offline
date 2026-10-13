package com.demo.mojioffline.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.demo.mojioffline.domain.model.AppLanguage
import com.demo.mojioffline.domain.model.ThemeMode
import com.demo.mojioffline.ui.i18n.LocalAppStrings
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val strings = LocalAppStrings.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SettingsEffect.Message -> snackbarHostState.showSnackbar(effect.text)
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(strings.settingsTitle) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SectionTitle(strings.settingsLanguage)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LanguageChip(AppLanguage.SYSTEM, state.preferences.language, strings.languageSystem) {
                    viewModel.onIntent(SettingsIntent.SetLanguage(it))
                }
                LanguageChip(AppLanguage.CHINESE, state.preferences.language, strings.languageZh) {
                    viewModel.onIntent(SettingsIntent.SetLanguage(it))
                }
                LanguageChip(AppLanguage.ENGLISH, state.preferences.language, strings.languageEn) {
                    viewModel.onIntent(SettingsIntent.SetLanguage(it))
                }
                LanguageChip(AppLanguage.JAPANESE, state.preferences.language, strings.languageJa) {
                    viewModel.onIntent(SettingsIntent.SetLanguage(it))
                }
            }

            SectionTitle(strings.settingsTheme)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeChip(ThemeMode.SYSTEM, state.preferences.themeMode, strings.themeSystem) {
                    viewModel.onIntent(SettingsIntent.SetTheme(it))
                }
                ThemeChip(ThemeMode.LIGHT, state.preferences.themeMode, strings.themeLight) {
                    viewModel.onIntent(SettingsIntent.SetTheme(it))
                }
                ThemeChip(ThemeMode.DARK, state.preferences.themeMode, strings.themeDark) {
                    viewModel.onIntent(SettingsIntent.SetTheme(it))
                }
            }

            SectionTitle(strings.settingsDebug)
            Text(strings.settingsForceError, style = MaterialTheme.typography.titleMedium)
            Text(
                strings.settingsForceErrorBody,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Switch(
                checked = state.preferences.forceHistoryError,
                onCheckedChange = { viewModel.onIntent(SettingsIntent.SetForceHistoryError(it)) },
            )

            SectionTitle(strings.settingsAbout)
            Text(
                strings.settingsAboutBody,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge)
}

@Composable
private fun LanguageChip(
    value: AppLanguage,
    selected: AppLanguage,
    label: String,
    onSelect: (AppLanguage) -> Unit,
) {
    FilterChip(
        selected = selected == value,
        onClick = { onSelect(value) },
        label = { Text(label) },
    )
}

@Composable
private fun ThemeChip(
    value: ThemeMode,
    selected: ThemeMode,
    label: String,
    onSelect: (ThemeMode) -> Unit,
) {
    FilterChip(
        selected = selected == value,
        onClick = { onSelect(value) },
        label = { Text(label) },
    )
}

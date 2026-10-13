package com.demo.mojioffline.feature.settings

import androidx.lifecycle.viewModelScope
import com.demo.mojioffline.core.mvi.MviViewModel
import com.demo.mojioffline.core.mvi.UiEffect
import com.demo.mojioffline.core.mvi.UiIntent
import com.demo.mojioffline.core.mvi.UiState
import com.demo.mojioffline.domain.model.AppLanguage
import com.demo.mojioffline.domain.model.AppPreferences
import com.demo.mojioffline.domain.model.ThemeMode
import com.demo.mojioffline.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class SettingsUiState(
    val preferences: AppPreferences = AppPreferences(),
) : UiState

sealed interface SettingsIntent : UiIntent {
    data class SetLanguage(val language: AppLanguage) : SettingsIntent
    data class SetTheme(val themeMode: ThemeMode) : SettingsIntent
    data class SetForceHistoryError(val enabled: Boolean) : SettingsIntent
}

sealed interface SettingsEffect : UiEffect {
    data class Message(val text: String) : SettingsEffect
}

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
) : MviViewModel<SettingsUiState, SettingsIntent, SettingsEffect>(SettingsUiState()) {

    init {
        preferencesRepository.observePreferences()
            .onEach { prefs -> setState { copy(preferences = prefs) } }
            .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.SetLanguage -> viewModelScope.launch {
                preferencesRepository.update { it.copy(language = intent.language) }
                sendEffect(SettingsEffect.Message("Language updated"))
            }

            is SettingsIntent.SetTheme -> viewModelScope.launch {
                preferencesRepository.update { it.copy(themeMode = intent.themeMode) }
                sendEffect(SettingsEffect.Message("Theme updated"))
            }

            is SettingsIntent.SetForceHistoryError -> viewModelScope.launch {
                preferencesRepository.update { it.copy(forceHistoryError = intent.enabled) }
            }
        }
    }
}

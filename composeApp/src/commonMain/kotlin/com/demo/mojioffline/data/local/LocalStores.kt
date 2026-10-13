package com.demo.mojioffline.data.local

import com.demo.mojioffline.core.result.StorageException
import com.demo.mojioffline.domain.model.AppLanguage
import com.demo.mojioffline.domain.model.AppPreferences
import com.demo.mojioffline.domain.model.HistoryEntry
import com.demo.mojioffline.domain.model.ThemeMode
import com.russhwolf.settings.Settings
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HistoryLocalStore(
    private val settings: Settings,
    private val json: Json = Json { ignoreUnknownKeys = true },
) {
    fun readAll(): List<HistoryEntry> {
        if (!settings.hasKey(KEY_HISTORY)) return emptyList()
        val raw = settings.getString(KEY_HISTORY, "")
        if (raw.isBlank()) return emptyList()
        return try {
            json.decodeFromString<List<HistoryEntry>>(raw)
        } catch (error: Exception) {
            throw StorageException("Failed to decode history: ${error.message}")
        }
    }

    fun writeAll(entries: List<HistoryEntry>) {
        try {
            settings.putString(KEY_HISTORY, json.encodeToString(entries))
        } catch (error: Exception) {
            throw StorageException("Failed to encode history: ${error.message}")
        }
    }

    fun clear() {
        settings.remove(KEY_HISTORY)
    }

    companion object {
        const val KEY_HISTORY = "history_entries_json"
    }
}

class PreferencesLocalStore(
    private val settings: Settings,
) {
    fun read(): AppPreferences = AppPreferences(
        language = settings.getString(KEY_LANGUAGE, AppLanguage.SYSTEM.name)
            .let { runCatching { AppLanguage.valueOf(it) }.getOrDefault(AppLanguage.SYSTEM) },
        themeMode = settings.getString(KEY_THEME, ThemeMode.SYSTEM.name)
            .let { runCatching { ThemeMode.valueOf(it) }.getOrDefault(ThemeMode.SYSTEM) },
        forceHistoryError = settings.getBoolean(KEY_FORCE_ERROR, false),
    )

    fun write(preferences: AppPreferences) {
        settings.putString(KEY_LANGUAGE, preferences.language.name)
        settings.putString(KEY_THEME, preferences.themeMode.name)
        settings.putBoolean(KEY_FORCE_ERROR, preferences.forceHistoryError)
    }

    companion object {
        const val KEY_LANGUAGE = "pref_language"
        const val KEY_THEME = "pref_theme"
        const val KEY_FORCE_ERROR = "pref_force_history_error"
    }
}

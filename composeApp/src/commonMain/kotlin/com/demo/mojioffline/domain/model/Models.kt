package com.demo.mojioffline.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class HistoryEntry(
    val id: String,
    val word: String,
    val reading: String,
    val meaningZh: String,
    val meaningEn: String,
    val meaningJa: String,
    val lookedUpAtEpochMs: Long,
)

enum class AppLanguage {
    SYSTEM,
    CHINESE,
    ENGLISH,
    JAPANESE,
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

@Serializable
data class AppPreferences(
    val language: AppLanguage = AppLanguage.SYSTEM,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val forceHistoryError: Boolean = false,
)

package com.demo.mojioffline.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.demo.mojioffline.domain.model.ThemeMode

private val LightColors = lightColorScheme(
    primary = Color(0xFF1F6F5B),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF3D5A80),
    background = Color(0xFFF7F4EF),
    surface = Color(0xFFFFFBF5),
    onBackground = Color(0xFF1C1B1A),
    onSurface = Color(0xFF1C1B1A),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7DCFB6),
    onPrimary = Color(0xFF00382C),
    secondary = Color(0xFF9BB1CE),
    background = Color(0xFF121412),
    surface = Color(0xFF1C1F1D),
    onBackground = Color(0xFFE8E6E1),
    onSurface = Color(0xFFE8E6E1),
)

@Composable
fun MojiTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit,
) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        content = content,
    )
}

package com.pawegio.homebudget.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// https://www.color-name.com/
object HomeBudgetPalette {
    val TuscanRed = Color(0xFF795548)
    val ChineseWhite = Color(0xFFE1E2E1)
    val Cultured = Color(0xFFF5F5F6)
}

val LightColors = lightColorScheme(
    primary = HomeBudgetPalette.TuscanRed,
    onPrimary = Color.White,
    surface = HomeBudgetPalette.TuscanRed,
    onSurface = Color.White,
    primaryContainer = HomeBudgetPalette.Cultured,
    onPrimaryContainer = Color.Black,
    background = HomeBudgetPalette.ChineseWhite,
)

val DarkColors = darkColorScheme()

@Composable
fun HomeBudgetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}

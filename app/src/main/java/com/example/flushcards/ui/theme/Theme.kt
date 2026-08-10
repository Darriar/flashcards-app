package com.example.flushcards.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ☀️ СВЕТЛАЯ ТЕМА
val LightColorScheme = lightColorScheme(
    primary = Ocean,
    onPrimary = Color.White,
    primaryContainer = SeaFoam,
    onPrimaryContainer = DeepWater,

    // ❇️ Верный ответ (Tertiary)
    tertiary = CorrectGreenLight,
    onTertiary = Color.White,
    tertiaryContainer = CorrectContainerLight,
    onTertiaryContainer = CorrectGreenLight,

    // 🛑 Неверный ответ (Error)
    error = IncorrectRedLight,
    onError = Color.White,
    errorContainer = IncorrectContainerLight,
    onErrorContainer = IncorrectRedLight,

    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = DeepWater,
    outline = Wave,
    outlineVariant = SeaFoam
)

// 🌙 ТЁМНАЯ ТЕМА
val DarkColorScheme = darkColorScheme(
    primary = Wave,
    onPrimary = Color(0xFF001F25),
    primaryContainer = DeepWater,
    onPrimaryContainer = SeaFoam,

    // ❇️ Верный ответ (Tertiary)
    tertiary = CorrectGreenDark,
    onTertiary = Color(0xFF003822),
    tertiaryContainer = CorrectContainerDark,
    onTertiaryContainer = CorrectGreenDark,

    // 🛑 Неверный ответ (Error)
    error = IncorrectRedDark,
    onError = Color(0xFF601410),
    errorContainer = IncorrectContainerDark,
    onErrorContainer = IncorrectRedDark,

    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = SeaFoam,
    outline = Wave,
    outlineVariant = Ocean
)

@Composable
fun FlushCardsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            //if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            if (darkTheme) DarkColorScheme else LightColorScheme
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
package com.example.habitflow.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = blue300,
    secondary = blue100,
    tertiary = grey100,
    background = grey350,
    primaryContainer = white,
    scrim = white,
    onSecondaryContainer = pink50,
    onPrimaryContainer = pink100,
    onPrimaryFixed = grey200,
    primaryFixedDim = blue900
)

private val LightColorScheme = lightColorScheme(
    primary = blue300,
    secondary = blue100,
    tertiary = grey100,
    background = grey350,
    scrim = white,
    onSecondaryContainer = pink50,
    onPrimaryContainer = pink100,
    primaryContainer = white,
    primaryFixedDim = blue900,
    onPrimaryFixed = grey200

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun HabitFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
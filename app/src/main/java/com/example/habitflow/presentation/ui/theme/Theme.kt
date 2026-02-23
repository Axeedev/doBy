package com.example.habitflow.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = blue500,
    background = blue500,
    secondaryContainer = blue400,
    onPrimaryContainer = blue400,
    onPrimary = grey150,
    tertiary = blue300,
    onPrimaryFixed = grey400,
    onTertiary = blue400,
    surfaceTint = grey800,
    onTertiaryFixedVariant = blue100,
    onSurface = grey300,
    onSecondaryContainer = blue500,
    onSurfaceVariant = grey150,
    tertiaryContainer = blue400,
    onPrimaryFixedVariant = grey100,
    scrim = blue100,
    surfaceContainer = blue300,
    tertiaryFixedDim = blue300,
    outline = grey150,
    surfaceBright = blue100,
    surfaceDim = orange500,
    onSecondaryFixedVariant = blue100,
    onSecondaryFixed = blue500,
    secondaryFixed = blue400,
    surfaceVariant = grey150
)

private val LightColorScheme = lightColorScheme(
    surfaceVariant = white,
    secondaryFixed = white,
    onSecondaryFixedVariant = blue500,
    onSecondaryFixed = white,
    surfaceDim = orange500,
    outline = grey100,
    surfaceBright = blue100,
    tertiaryFixedDim = green100,
    scrim = green500,
    onTertiaryFixedVariant = white,
    onSurfaceVariant = grey700,
    primary = white,
    onPrimaryFixedVariant = white,
    secondaryContainer = pink300,
    onSecondaryContainer = pink400,
    onPrimaryContainer = white,
    tertiaryContainer = white,
    background = grey100,
    surfaceContainer = grey100,
    onPrimary = black,
    onPrimaryFixed = grey400,
    tertiary = green500,
    onTertiary = grey250,
    surfaceTint = grey200,
    onSurface = grey300

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
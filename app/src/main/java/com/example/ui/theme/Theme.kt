package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NurseAidLightColorScheme = lightColorScheme(
    primary = NurviaRoyalBlue,
    onPrimary = Color.White,
    primaryContainer = NurviaSoftContainer,
    onPrimaryContainer = NurviaSapphireDark,
    secondary = NurviaCyanGlow,
    onSecondary = Color.White,
    secondaryContainer = NurviaIceBlue,
    onSecondaryContainer = NurviaSapphireDark,
    tertiary = WarmAmber,
    onTertiary = Color.White,
    tertiaryContainer = AmberLight,
    onTertiaryContainer = Color(0xFF78350F),
    error = EmergencyRose,
    onError = Color.White,
    errorContainer = EmergencyRoseLight,
    onErrorContainer = EmergencyRoseDark,
    background = SlateBackground,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = SlateSubtle,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NurseAidLightColorScheme,
        typography = Typography,
        content = content
    )
}

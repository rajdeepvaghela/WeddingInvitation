package com.rdapps.weddinginvitation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font
import wedding_invitation.composeapp.generated.resources.*
import wedding_invitation.composeapp.generated.resources.Allura_Regular
import wedding_invitation.composeapp.generated.resources.Res

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

internal val LocalThemeIsDark = compositionLocalOf { mutableStateOf(true) }

@Composable
internal fun AppTheme(
    content: @Composable () -> Unit
) {
    val systemIsDark = isSystemInDarkTheme()
    val isDarkState = remember { mutableStateOf(systemIsDark) }

    val alluraFont = FontFamily(Font(Res.font.Allura_Regular))
    val satisfyFont = FontFamily(Font(Res.font.Satisfy_Regular))

    fun TextStyle.setFont(fontFamily: FontFamily) = copy(fontFamily = fontFamily)

    @Composable
    fun getTypography(fontFamily: FontFamily) = Typography(
        displayLarge = MaterialTheme.typography.displayLarge.setFont(alluraFont),
        displayMedium = MaterialTheme.typography.displayMedium.setFont(alluraFont),
        displaySmall = MaterialTheme.typography.displaySmall.setFont(alluraFont),
        headlineLarge = MaterialTheme.typography.headlineLarge.setFont(fontFamily),
        headlineMedium = MaterialTheme.typography.headlineMedium.setFont(fontFamily),
        headlineSmall = MaterialTheme.typography.headlineSmall.setFont(fontFamily),
        titleLarge = MaterialTheme.typography.titleLarge.setFont(fontFamily),
        titleMedium = MaterialTheme.typography.titleMedium.setFont(fontFamily),
        titleSmall = MaterialTheme.typography.titleSmall.setFont(fontFamily),
        bodyLarge = MaterialTheme.typography.bodyLarge.setFont(fontFamily),
        bodyMedium = MaterialTheme.typography.bodyMedium.setFont(fontFamily),
        bodySmall = MaterialTheme.typography.bodySmall.setFont(fontFamily),
        labelLarge = MaterialTheme.typography.labelLarge.setFont(fontFamily),
        labelMedium = MaterialTheme.typography.labelMedium.setFont(fontFamily),
        labelSmall = MaterialTheme.typography.labelSmall.setFont(fontFamily),
    )

    CompositionLocalProvider(
        LocalThemeIsDark provides isDarkState
    ) {
        val isDark by isDarkState
        SystemAppearance(!isDark)
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = getTypography(satisfyFont),
            content = { Surface(content = content) }
        )
    }
}


@Composable
internal expect fun SystemAppearance(isDark: Boolean)

package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = Green80,
    onPrimary = Color(0xFF00382B),
    primaryContainer = WhatsAppGreenDark,
    onPrimaryContainer = Mint80,
    secondary = GreenGrey80,
    onSecondary = Color(0xFF1B352A),
    secondaryContainer = Color(0xFF314C40),
    onSecondaryContainer = Color(0xFFD0E8DA),
    tertiary = Mint80,
    background = SurfaceDark,
    surface = SurfaceDark,
    surfaceVariant = CardSurfaceDark,
    onBackground = Color(0xFFE1E3E1),
    onSurface = Color(0xFFE1E3E1),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = WhatsAppGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB9F1DE),
    onPrimaryContainer = Color(0xFF002018),
    secondary = WhatsAppTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD3E7DC),
    onSecondaryContainer = Color(0xFF072017),
    tertiary = WhatsAppEmerald,
    background = SurfaceLight,
    surface = CardSurfaceLight,
    surfaceVariant = Color(0xFFE7ECE9),
    onBackground = Color(0xFF191C1B),
    onSurface = Color(0xFF191C1B),
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Keep branded colors for high fidelity WhatsApp feel
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}


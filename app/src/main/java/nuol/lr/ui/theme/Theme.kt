package nuol.lr.ui.theme

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

private val LightColorScheme = lightColorScheme(primary = md_theme_light_primary, onPrimary = md_theme_light_onPrimary, primaryContainer = md_theme_light_primaryContainer, onPrimaryContainer = md_theme_light_onPrimaryContainer, background = md_theme_light_background, onBackground = md_theme_light_onBackground)
private val DarkColorScheme = darkColorScheme(primary = md_theme_dark_primary, onPrimary = md_theme_dark_onPrimary, primaryContainer = md_theme_dark_primaryContainer, onPrimaryContainer = md_theme_dark_onPrimaryContainer, background = md_theme_dark_background, onBackground = md_theme_dark_onBackground)

// YENİ: OLED Saf Siyah Renk Paleti (Pilleri %100 kapatır)
private val OledColorScheme = darkColorScheme(primary = md_theme_dark_primary, onPrimary = md_theme_dark_onPrimary, primaryContainer = md_theme_dark_primaryContainer, onPrimaryContainer = md_theme_dark_onPrimaryContainer, background = Color.Black, surface = Color.Black, onBackground = Color.White)

@Composable
fun NuoLTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useMaterialYou: Boolean = true,
    isOled: Boolean = false, // YENİ
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        isOled -> OledColorScheme
        useMaterialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, shapes = Shapes, content = content)
}

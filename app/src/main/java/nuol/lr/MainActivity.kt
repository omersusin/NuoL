package nuol.lr

import android.appwidget.AppWidgetHost
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import nuol.lr.ui.home.HomeScreen
import nuol.lr.ui.home.HomeViewModel
import nuol.lr.ui.theme.NuoLTheme

// YENİ: BiometricPrompt kullanabilmek için AppCompatActivity (FragmentActivity türevi) yapıldı
class MainActivity : AppCompatActivity() {
    private lateinit var appWidgetHost: AppWidgetHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appWidgetHost = AppWidgetHost(this, 1024)
        
        enableEdgeToEdge()
        setContent {
            val viewModel: HomeViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            val showStatusBar by viewModel.showStatusBar.collectAsState()
            val useMaterialYou by viewModel.useMaterialYou.collectAsState()
            
            val isDark = when(themeMode) { 1 -> false; 2, 3 -> true; else -> isSystemInDarkTheme() }
            val isOled = themeMode == 3 // YENİ: OLED Saf Siyah Modu

            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = this.window
                    val insetsController = WindowCompat.getInsetsController(window, view)
                    if (showStatusBar) insetsController.show(WindowInsetsCompat.Type.statusBars())
                    else insetsController.hide(WindowInsetsCompat.Type.statusBars())
                }
            }

            NuoLTheme(darkTheme = isDark, useMaterialYou = useMaterialYou, isOled = isOled) {
                HomeScreen(viewModel = viewModel, appWidgetHost = appWidgetHost)
            }
        }
    }
    override fun onStart() { super.onStart(); appWidgetHost.startListening() }
    override fun onStop() { super.onStop(); appWidgetHost.stopListening() }
}

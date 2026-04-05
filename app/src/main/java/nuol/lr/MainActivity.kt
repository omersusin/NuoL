package nuol.lr

import android.appwidget.AppWidgetHost
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import nuol.lr.ui.home.HomeScreen
import nuol.lr.ui.home.HomeViewModel
import nuol.lr.ui.theme.NuoLTheme

class MainActivity : ComponentActivity() {
    
    // YENİ: Widget barındırıcı motoru. 1024 standart ID'dir.
    private lateinit var appWidgetHost: AppWidgetHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appWidgetHost = AppWidgetHost(this, 1024)
        
        enableEdgeToEdge()
        setContent {
            val viewModel: HomeViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            val isDark = when(themeMode) { 1 -> false; 2 -> true; else -> isSystemInDarkTheme() }

            NuoLTheme(darkTheme = isDark) {
                // appWidgetHost parametresini arayüze geçiriyoruz
                HomeScreen(viewModel = viewModel, appWidgetHost = appWidgetHost)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        appWidgetHost.startListening() // Widget'ları canlandırır (Örn: Saat tiklemesi)
    }

    override fun onStop() {
        super.onStop()
        appWidgetHost.stopListening() // Arka planda pil tüketimini engeller
    }
}

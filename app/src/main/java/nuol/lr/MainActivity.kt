package nuol.lr

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: HomeViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            
            // 0: Sistem, 1: Açık, 2: Koyu
            val isDark = when(themeMode) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }

            NuoLTheme(darkTheme = isDark) {
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}

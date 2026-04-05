package nuol.lr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import nuol.lr.ui.home.HomeScreen
import nuol.lr.ui.theme.NuoLTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Edge-to-Edge: Durum çubuğunu ve navigasyon çubuğunu şeffaf yapar
        enableEdgeToEdge()
        
        setContent {
            NuoLTheme {
                // Temel katman tamamen şeffaf, böylece sistemin duvar kağıdı görünür!
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent) 
                ) {
                    HomeScreen()
                }
            }
        }
    }
}

package nuol.lr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import nuol.lr.ui.theme.NuoLTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Bir Launcher için en önemli ayar: Tam Ekran ve Saydam Barlar
        enableEdgeToEdge() 
        
        setContent {
            NuoLTheme {
                // Arka planı tamamen saydam yapıyoruz ki cihazın duvar kağıdı görünsün
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NuoL Launcher'a Hoş Geldiniz",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

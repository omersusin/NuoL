package nuol.lr.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val apps by viewModel.apps.collectAsState()
    
    // Çekmecenin açık/kapalı durumunu tutan State
    var isDrawerOpen by remember { mutableStateOf(false) }
    
    // BottomSheet animasyon yöneticisi (tam ekran açılması için skipPartiallyExpanded = true)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Arka planı tamamen şeffaf tutuyoruz ki cihazın kendi duvar kağıdı arkada görünsün
    Column(modifier = Modifier.fillMaxSize()) {
        
        // Üst kısım: Çalışma Alanı (Yukarı kaydırma dinleyicisine sahip)
        Workspace(
            modifier = Modifier.weight(1f),
            onSwipeUp = { isDrawerOpen = true }
        )

        // Alt kısım: Dock
        if (apps.isNotEmpty()) {
            Dock(apps = apps)
        }
    }

    // Çekmece Açıkken Gösterilecek Arayüz
    if (isDrawerOpen) {
        ModalBottomSheet(
            onDismissRequest = { isDrawerOpen = false },
            sheetState = sheetState,
            // Çekmecenin arka plan rengi ve hafif saydamlığı
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
            // Çekmece açıkken arka planı hafif karartır (Modern Launcher stili)
            scrimColor = Color.Black.copy(alpha = 0.4f),
            windowInsets = WindowInsets.statusBars
        ) {
            // 4. Adımda yaptığımız AppDrawer'ı buraya çağırıyoruz
            AppDrawer(viewModel = viewModel)
        }
    }
}

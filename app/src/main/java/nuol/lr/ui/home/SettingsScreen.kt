package nuol.lr.ui.home

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: HomeViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val drawerCols by viewModel.drawerColumns.collectAsState()
    val homeCols by viewModel.homeColumns.collectAsState()
    val iconSize by viewModel.iconSize.collectAsState()
    val showLabels by viewModel.showLabels.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("NuoL Ayarları") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri") } }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            
            // 1. Temel Ayarlar (Varsayılan Launcher Yap)
            Text("Temel Ayarlar", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(16.dp))
            Button(
                onClick = { context.startActivity(Intent(Settings.ACTION_HOME_SETTINGS)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) { Text("Varsayılan Launcher Olarak Ayarla") }
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // 2. Görünüm Ayarları
            Text("Görünüm ve Tema", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(16.dp))
            
            ListItem(
                headlineContent = { Text("Tema Modu") },
                supportingContent = { Text(when(themeMode) { 1 -> "Açık Tema"; 2 -> "Koyu Tema"; else -> "Sistem Varsayılanı" }) },
                modifier = Modifier.clickable { 
                    val next = if(themeMode == 2) 0 else themeMode + 1
                    viewModel.setThemeMode(next) 
                }
            )

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("İkon Boyutu: ${iconSize}dp")
                Slider(value = iconSize.toFloat(), onValueChange = { viewModel.setIconSize(it.toInt()) }, valueRange = 40f..80f)
            }

            ListItem(
                headlineContent = { Text("Uygulama İsimlerini Göster") },
                trailingContent = { Switch(checked = showLabels, onCheckedChange = { viewModel.setShowLabels(it) }) }
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // 3. Izgara Ayarları
            Text("Ana Ekran & Çekmece (Grid)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Ana Ekran Sütun Sayısı: $homeCols")
                Slider(value = homeCols.toFloat(), onValueChange = { viewModel.setHomeCols(it.toInt()) }, valueRange = 3f..7f, steps = 3)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Çekmece Sütun Sayısı: $drawerCols")
                Slider(value = drawerCols.toFloat(), onValueChange = { viewModel.setDrawerCols(it.toInt()) }, valueRange = 3f..7f, steps = 3)
            }
        }
    }
}

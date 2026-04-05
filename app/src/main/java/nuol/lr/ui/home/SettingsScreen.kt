package nuol.lr.ui.home

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
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: HomeViewModel, onBack: () -> Unit) {
    val drawerCols by viewModel.drawerColumns.collectAsState()
    val homeCols by viewModel.homeColumns.collectAsState()
    val iconPacks by viewModel.iconPacks.collectAsState()
    val currentPack by viewModel.currentIconPack.collectAsState()
    
    var showIconDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NuoL Ayarları") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            
            // Izgara (Grid) Ayarları
            Text("Ana Ekran ve Çekmece", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(16.dp))
            
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Ana Ekran Sütun Sayısı: $homeCols")
                Slider(value = homeCols.toFloat(), onValueChange = { viewModel.setHomeCols(it.toInt()) }, valueRange = 3f..7f, steps = 3)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Uygulama Çekmecesi Sütun Sayısı: $drawerCols")
                Slider(value = drawerCols.toFloat(), onValueChange = { viewModel.setDrawerCols(it.toInt()) }, valueRange = 3f..7f, steps = 3)
            }
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Görünüm (Tema/İkon) Ayarları
            Text("Görünüm", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(16.dp))
            
            ListItem(
                headlineContent = { Text("İkon Paketi") },
                supportingContent = { Text(iconPacks.find { it.packageName == currentPack }?.label ?: "Sistem Varsayılanı") },
                modifier = Modifier.clickable { showIconDialog = true }
            )
            
            if (showIconDialog) {
                AlertDialog(
                    onDismissRequest = { showIconDialog = false },
                    title = { Text("İkon Paketi Seç") },
                    text = {
                        Column {
                            Text("Sistem Varsayılanı", modifier = Modifier.fillMaxWidth().clickable { viewModel.applyIconPack(null); showIconDialog = false }.padding(16.dp))
                            Divider()
                            iconPacks.forEach { pack ->
                                Text(pack.label, modifier = Modifier.fillMaxWidth().clickable { viewModel.applyIconPack(pack.packageName); showIconDialog = false }.padding(16.dp))
                            }
                        }
                    },
                    confirmButton = { TextButton(onClick = { showIconDialog = false }) { Text("Kapat") } }
                )
            }
        }
    }
}

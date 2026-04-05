package nuol.lr.ui.home

import android.content.Intent
import android.provider.Settings
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: HomeViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val drawerCols by viewModel.drawerColumns.collectAsState()
    val homeCols by viewModel.homeColumns.collectAsState()
    val iconSize by viewModel.iconSize.collectAsState()
    val showLabels by viewModel.showLabels.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val showStatusBar by viewModel.showStatusBar.collectAsState()
    val allAppsWithHiddenState by viewModel.allAppsWithHiddenState.collectAsState()
    val drawerOpacity by viewModel.drawerOpacity.collectAsState()
    val appSortMode by viewModel.appSortMode.collectAsState()
    val doubleTapAction by viewModel.doubleTapAction.collectAsState()
    val iconShape by viewModel.iconShape.collectAsState()
    val enableBlur by viewModel.enableBlur.collectAsState()
    val useMaterialYou by viewModel.useMaterialYou.collectAsState()
    val bottomSearchBar by viewModel.bottomSearchBar.collectAsState()
    val hapticFeedback by viewModel.hapticFeedback.collectAsState()

    var showHiddenAppsDialog by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("NuoL Ayarları") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri") } }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            
            Text("Gestures & Efektler (Premium)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(16.dp))
            ListItem(headlineContent = { Text("Dokunsal Geri Bildirim (Titreşim)") }, trailingContent = { Switch(checked = hapticFeedback, onCheckedChange = { viewModel.setHapticFeedback(it) }) })
            ListItem(headlineContent = { Text("Cam Bulanıklığı (Blur) Efekti") }, trailingContent = { Switch(checked = enableBlur, onCheckedChange = { viewModel.setEnableBlur(it) }) })
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Görünüm ve İkonlar", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(16.dp))
            ListItem(headlineContent = { Text("Tema Modu") }, supportingContent = { Text(when(themeMode) { 1 -> "Açık"; 2 -> "Koyu"; 3 -> "OLED Saf Siyah"; else -> "Sistem" }) }, modifier = Modifier.clickable { val next = if(themeMode == 3) 0 else themeMode + 1; viewModel.setThemeMode(next) })
            ListItem(headlineContent = { Text("Durum Çubuğunu Göster") }, trailingContent = { Switch(checked = showStatusBar, onCheckedChange = { viewModel.setShowStatusBar(it) }) })
            Column(modifier = Modifier.padding(horizontal = 16.dp)) { Text("İkon Boyutu: ${iconSize}dp"); Slider(value = iconSize.toFloat(), onValueChange = { viewModel.setIconSize(it.toInt()) }, valueRange = 40f..80f) }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Güvenlik (Biyometrik Korumalı)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(16.dp))
            ListItem(
                headlineContent = { Text("Gizlenen Uygulamalar") },
                supportingContent = { Text("Girmek için parmak izi gerekir") },
                modifier = Modifier.clickable { 
                    // YENİ: Parmak İzi Doğrulama Tetikleyici
                    val executor = ContextCompat.getMainExecutor(context)
                    val biometricPrompt = BiometricPrompt(context as FragmentActivity, executor, object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            showHiddenAppsDialog = true // Sadece başarılıysa açılır!
                        }
                    })
                    val promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle("Gizli Uygulamalar").setSubtitle("Kimliğinizi doğrulayın").setNegativeButtonText("İptal").build()
                    biometricPrompt.authenticate(promptInfo)
                }
            )

            if (showHiddenAppsDialog) {
                AlertDialog(onDismissRequest = { showHiddenAppsDialog = false }, title = { Text("Gizli Uygulamalar") }, text = { LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) { items(allAppsWithHiddenState) { (app, isHidden) -> Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) { Row(verticalAlignment = Alignment.CenterVertically) { AsyncImage(model = app.icon, contentDescription = null, modifier = Modifier.size(32.dp)); Spacer(modifier = Modifier.width(12.dp)); Text(app.label) }; Switch(checked = isHidden, onCheckedChange = { hide -> if (hide) viewModel.hideApp(app.packageName) else viewModel.unhideApp(app.packageName) }) } } } }, confirmButton = { TextButton(onClick = { showHiddenAppsDialog = false }) { Text("Kapat") } })
            }
        }
    }
}

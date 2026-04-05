package nuol.lr.ui.home

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer(viewModel: HomeViewModel, closeDrawer: () -> Unit, iconShape: Int) {
    val filteredApps by viewModel.filteredApps.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val drawerCols by viewModel.drawerColumns.collectAsState()
    val iconSize by viewModel.iconSize.collectAsState()
    val showLabels by viewModel.showLabels.collectAsState()
    val context = LocalContext.current

    var appToRename by remember { mutableStateOf<nuol.lr.core.AppInfo?>(null) }
    var renameText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
        TextField(
            value = searchQuery, onValueChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), placeholder = { Text("Uygulama Ara...") }, leadingIcon = { Icon(Icons.Default.Search, "Ara") },
            trailingIcon = { if (searchQuery.isNotEmpty()) IconButton(onClick = { viewModel.onSearchQueryChange("") }) { Icon(Icons.Default.Clear, "Temizle") } },
            shape = RoundedCornerShape(28.dp), colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // YENİ: Akıllı Derin Arama (Bulunamazsa)
        if (filteredApps.isEmpty() && searchQuery.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("'$searchQuery' bulunamadı.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { context.startActivity(Intent(Intent.ACTION_WEB_SEARCH).putExtra(SearchManager.QUERY, searchQuery).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Public, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Google'da Ara")
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=$searchQuery")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                    Icon(Icons.Default.Shop, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Play Store'da Ara")
                }
            }
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(drawerCols), contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
                items(filteredApps) { app ->
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        AppItemUI(app = app, iconSize = iconSize, showLabel = showLabels, iconShape = iconShape,
                            onClick = { context.packageManager.getLaunchIntentForPackage(app.packageName)?.let { context.startActivity(it); closeDrawer() } },
                            onLongClick = { expanded = true }
                        )
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(text = { Text("Ana Ekrana Ekle") }, onClick = { expanded = false; viewModel.pinAppToHome(app.packageName); closeDrawer() })
                            DropdownMenuItem(text = { Text("Dock'a Ekle") }, onClick = { expanded = false; viewModel.pinAppToDock(app.packageName); closeDrawer() })
                            Divider()
                            // YENİ: İsim Değiştirme
                            DropdownMenuItem(text = { Text("Yeniden Adlandır") }, onClick = { expanded = false; appToRename = app; renameText = app.label })
                            DropdownMenuItem(text = { Text("Uygulamayı Gizle") }, onClick = { expanded = false; viewModel.hideApp(app.packageName) })
                            DropdownMenuItem(text = { Text("Uygulama Bilgisi") }, onClick = { expanded = false; context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${app.packageName}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); closeDrawer() })
                        }
                    }
                }
            }
        }
    }

    // İsim Değiştirme Dialogu
    if (appToRename != null) {
        AlertDialog(
            onDismissRequest = { appToRename = null }, title = { Text("Yeniden Adlandır") },
            text = { TextField(value = renameText, onValueChange = { renameText = it }, singleLine = true) },
            confirmButton = { TextButton(onClick = { viewModel.renameApp(appToRename!!.packageName, renameText.trim()); appToRename = null }) { Text("Kaydet") } },
            dismissButton = { TextButton(onClick = { viewModel.renameApp(appToRename!!.packageName, ""); appToRename = null }) { Text("Varsayılana Dön") } }
        )
    }
}

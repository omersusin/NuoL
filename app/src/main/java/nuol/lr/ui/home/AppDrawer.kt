package nuol.lr.ui.home

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer(viewModel: HomeViewModel, closeDrawer: () -> Unit) {
    val filteredApps by viewModel.filteredApps.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val drawerCols by viewModel.drawerColumns.collectAsState()
    val iconSize by viewModel.iconSize.collectAsState()
    val showLabels by viewModel.showLabels.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
        TextField(
            value = searchQuery, onValueChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            placeholder = { Text("Ara...") },
            leadingIcon = { Icon(Icons.Default.Search, "Ara") },
            trailingIcon = { if (searchQuery.isNotEmpty()) IconButton(onClick = { viewModel.onSearchQueryChange("") }) { Icon(Icons.Default.Clear, "Temizle") } },
            shape = RoundedCornerShape(28.dp),
            colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(drawerCols), contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
            items(filteredApps) { app ->
                var expanded by remember { mutableStateOf(false) }
                Box {
                    AppItemUI(app = app, iconSize = iconSize, showLabel = showLabels,
                        onClick = { context.packageManager.getLaunchIntentForPackage(app.packageName)?.let { context.startActivity(it); closeDrawer() } },
                        onLongClick = { expanded = true }
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("Ana Ekrana Ekle") }, onClick = { expanded = false; viewModel.pinAppToHome(app.packageName); closeDrawer() })
                        DropdownMenuItem(text = { Text("Dock'a Ekle") }, onClick = { expanded = false; viewModel.pinAppToDock(app.packageName); closeDrawer() })
                        Divider()
                        // YENİ: Uygulamayı Gizle Seçeneği
                        DropdownMenuItem(text = { Text("Uygulamayı Gizle") }, onClick = { expanded = false; viewModel.hideApp(app.packageName); closeDrawer() })
                        DropdownMenuItem(text = { Text("Uygulama Bilgisi") }, onClick = { expanded = false; context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${app.packageName}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); closeDrawer() })
                    }
                }
            }
        }
    }
}

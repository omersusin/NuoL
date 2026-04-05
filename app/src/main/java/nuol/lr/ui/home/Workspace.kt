package nuol.lr.ui.home

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import nuol.lr.core.AppInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Workspace(
    modifier: Modifier = Modifier,
    onSwipeUp: () -> Unit,
    pinnedApps: List<AppInfo>,
    homeColumns: Int,
    onUnpinApp: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    var showWorkspaceMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { showWorkspaceMenu = true },
                    onPress = { /* Kaydırma işlemleri için eklenebilir */ }
                )
            }
    ) {
        // Ana Ekran Izgarası (Dinamik Sütun Sayısı)
        LazyVerticalGrid(
            columns = GridCells.Fixed(homeColumns),
            modifier = Modifier.fillMaxWidth().align(Alignment.Center).padding(horizontal = 8.dp, vertical = 32.dp)
        ) {
            items(pinnedApps) { app ->
                var expanded by remember { mutableStateOf(false) }
                Box {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .combinedClickable(
                                onClick = {
                                    val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                    intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)?.let { context.startActivity(it) }
                                },
                                onLongClick = { expanded = true }
                            ).padding(8.dp)
                    ) {
                        AsyncImage(model = app.icon, contentDescription = app.label, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(app.label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                    }

                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("Kaldır", color = MaterialTheme.colorScheme.error) }, onClick = { expanded = false; onUnpinApp(app.packageName) })
                    }
                }
            }
        }

        // Ana Ekrana Uzun Basınca Açılan Menü
        if (showWorkspaceMenu) {
            AlertDialog(
                onDismissRequest = { showWorkspaceMenu = false },
                title = { Text("Ana Ekran") },
                text = {
                    Column {
                        Text("NuoL Ayarları", modifier = Modifier.fillMaxWidth().clickable { showWorkspaceMenu = false; onOpenSettings() }.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                        Divider()
                        Text("Duvar Kağıtları", modifier = Modifier.fillMaxWidth().clickable { 
                            showWorkspaceMenu = false
                            val intent = Intent(Intent.ACTION_SET_WALLPAPER)
                            context.startActivity(Intent.createChooser(intent, "Duvar Kağıdı Seç"))
                        }.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                    }
                },
                confirmButton = { TextButton(onClick = { showWorkspaceMenu = false }) { Text("İptal") } }
            )
        }
    }
}

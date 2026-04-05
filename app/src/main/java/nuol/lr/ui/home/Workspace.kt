package nuol.lr.ui.home

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import nuol.lr.core.AppInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Workspace(
    modifier: Modifier = Modifier,
    onSwipeUp: () -> Unit,
    pinnedApps: List<AppInfo>,
    homeColumns: Int,
    iconSize: Int,
    showLabels: Boolean,
    onUnpinApp: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    var showWorkspaceMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { showWorkspaceMenu = true })
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -20) onSwipeUp() // Yukarı Kaydır: Çekmece
                    if (dragAmount > 30) {
                        // Aşağı Kaydır: Bildirim Paneli (Sistem metodunu tetikler)
                        try {
                            val service = context.getSystemService("statusbar")
                            val statusbarManager = Class.forName("android.app.StatusBarManager")
                            val expand = statusbarManager.getMethod("expandNotificationsPanel")
                            expand.invoke(service)
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                }
            }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(homeColumns),
            modifier = Modifier.fillMaxWidth().align(Alignment.Center).padding(horizontal = 8.dp, vertical = 32.dp)
        ) {
            items(pinnedApps) { app ->
                var expanded by remember { mutableStateOf(false) }
                Box {
                    AppItemUI(app = app, iconSize = iconSize, showLabel = showLabels,
                        onClick = { context.packageManager.getLaunchIntentForPackage(app.packageName)?.let { context.startActivity(it) } },
                        onLongClick = { expanded = true }
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("Kaldır", color = MaterialTheme.colorScheme.error) }, onClick = { expanded = false; onUnpinApp(app.packageName) })
                    }
                }
            }
        }

        if (showWorkspaceMenu) {
            AlertDialog(
                onDismissRequest = { showWorkspaceMenu = false }, title = { Text("Ana Ekran") },
                text = {
                    Column {
                        Text("NuoL Ayarları", modifier = Modifier.fillMaxWidth().clickable { showWorkspaceMenu = false; onOpenSettings() }.padding(16.dp))
                    }
                },
                confirmButton = { TextButton(onClick = { showWorkspaceMenu = false }) { Text("İptal") } }
            )
        }
    }
}

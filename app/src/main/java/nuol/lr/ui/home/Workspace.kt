package nuol.lr.ui.home

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
    widgetIds: List<Int>, // YENİ
    appWidgetHost: AppWidgetHost, // YENİ
    homeColumns: Int,
    iconSize: Int,
    showLabels: Boolean,
    onUnpinApp: (String) -> Unit,
    onAddWidget: (Int) -> Unit, // YENİ
    onRemoveWidget: (Int) -> Unit, // YENİ
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    var showWorkspaceMenu by remember { mutableStateOf(false) }

    // YENİ: Sistem Widget Seçicisini çağıran Launcher
    val widgetPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val appWidgetId = result.data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1
        if (result.resultCode == Activity.RESULT_OK && appWidgetId != -1) {
            onAddWidget(appWidgetId)
        } else if (appWidgetId != -1) {
            appWidgetHost.deleteAppWidgetId(appWidgetId) // İptal edildiyse hafızayı temizle
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onLongPress = { showWorkspaceMenu = true }) }
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -20) onSwipeUp()
                    if (dragAmount > 30) {
                        try {
                            val service = context.getSystemService("statusbar")
                            val statusbarManager = Class.forName("android.app.StatusBarManager")
                            statusbarManager.getMethod("expandNotificationsPanel").invoke(service)
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                }
            }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(homeColumns),
            modifier = Modifier.fillMaxWidth().align(Alignment.Center).padding(horizontal = 8.dp, vertical = 32.dp)
        ) {
            // YENİ: Widget'ları ızgaranın en üstüne tam satır yayarak çiziyoruz
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    widgetIds.forEach { id ->
                        AndroidWidget(appWidgetHost = appWidgetHost, appWidgetId = id, onRemove = { onRemoveWidget(id) })
                    }
                }
            }

            // Normal Uygulamalar
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
                        // YENİ: Widget Ekleme Butonu
                        Text("Widget Ekle", modifier = Modifier.fillMaxWidth().clickable { 
                            showWorkspaceMenu = false
                            val appWidgetId = appWidgetHost.allocateAppWidgetId()
                            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                            widgetPickerLauncher.launch(intent)
                        }.padding(16.dp))
                        Divider()
                        Text("NuoL Ayarları", modifier = Modifier.fillMaxWidth().clickable { showWorkspaceMenu = false; onOpenSettings() }.padding(16.dp))
                    }
                },
                confirmButton = { TextButton(onClick = { showWorkspaceMenu = false }) { Text("İptal") } }
            )
        }
    }
}

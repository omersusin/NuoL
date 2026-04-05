package nuol.lr.ui.home

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
import nuol.lr.core.AppInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Workspace(
    modifier: Modifier = Modifier,
    onSwipeUp: () -> Unit,
    pinnedApps: List<AppInfo>, widgetIds: List<Int>, appWidgetHost: AppWidgetHost,
    homeColumns: Int, iconSize: Int, showLabels: Boolean, iconShape: Int, doubleTapAction: Int,
    hapticFeedback: Boolean, // YENİ
    onUnpinApp: (String) -> Unit, onAddWidget: (Int) -> Unit, onRemoveWidget: (Int) -> Unit, onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var showWorkspaceMenu by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    val currentDate = dateFormat.format(Date())
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) { in 5..11 -> "Günaydın,"; in 12..17 -> "İyi Günler,"; in 18..22 -> "İyi Akşamlar,"; else -> "İyi Geceler," }

    var batteryLevel by remember { mutableStateOf(100) }
    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() { override fun onReceive(c: Context?, intent: Intent?) { val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1; val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1; if (level != -1 && scale != -1) batteryLevel = (level * 100 / scale.toFloat()).toInt() } }
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED)); onDispose { context.unregisterReceiver(receiver) }
    }

    val widgetPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val appWidgetId = result.data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1
        if (result.resultCode == Activity.RESULT_OK && appWidgetId != -1) onAddWidget(appWidgetId) else if (appWidgetId != -1) appWidgetHost.deleteAppWidgetId(appWidgetId)
    }

    Box(
        modifier = modifier.fillMaxSize().pointerInput(Unit) { 
                detectTapGestures(
                    onLongPress = { if(hapticFeedback) haptic.performHapticFeedback(HapticFeedbackType.LongPress); showWorkspaceMenu = true },
                    onDoubleTap = { when (doubleTapAction) { 1 -> onOpenSettings(); 2 -> onSwipeUp(); 3 -> { try { val service = context.getSystemService("statusbar"); Class.forName("android.app.StatusBarManager").getMethod("expandNotificationsPanel").invoke(service) } catch (e: Exception) {} } } }
                ) 
            }.pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -20) onSwipeUp()
                    if (dragAmount > 30) { try { val service = context.getSystemService("statusbar"); Class.forName("android.app.StatusBarManager").getMethod("expandNotificationsPanel").invoke(service) } catch (e: Exception) {} }
                }
            }
    ) {
        Column(modifier = Modifier.padding(top = 80.dp, start = 24.dp).align(Alignment.TopStart)) {
            Text(greeting, style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)).padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(currentDate, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.clickable { try { context.startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALENDAR).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } catch (e: Exception) {} })
                Spacer(modifier = Modifier.width(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { try { context.startActivity(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } catch (e: Exception) {} }) {
                    Icon(Icons.Default.BatteryFull, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)); Spacer(modifier = Modifier.width(4.dp)); Text("%$batteryLevel", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyVerticalGrid(columns = GridCells.Fixed(homeColumns), modifier = Modifier.fillMaxWidth().align(Alignment.Center).padding(horizontal = 8.dp, vertical = 32.dp)) {
            item(span = { GridItemSpan(maxLineSpan) }) { Column { widgetIds.forEach { id -> AndroidWidget(appWidgetHost = appWidgetHost, appWidgetId = id, onRemove = { onRemoveWidget(id) }) } } }
            items(pinnedApps) { app ->
                var expanded by remember { mutableStateOf(false) }
                Box {
                    AppItemUI(app = app, iconSize = iconSize, showLabel = showLabels, iconShape = iconShape, onClick = { context.packageManager.getLaunchIntentForPackage(app.packageName)?.let { context.startActivity(it) } }, onLongClick = { if(hapticFeedback) haptic.performHapticFeedback(HapticFeedbackType.LongPress); expanded = true })
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) { DropdownMenuItem(text = { Text("Kaldır", color = MaterialTheme.colorScheme.error) }, onClick = { expanded = false; onUnpinApp(app.packageName) }) }
                }
            }
        }

        if (showWorkspaceMenu) {
            AlertDialog(onDismissRequest = { showWorkspaceMenu = false }, title = { Text("Ana Ekran") }, text = { Column { Text("Widget Ekle", modifier = Modifier.fillMaxWidth().clickable { showWorkspaceMenu = false; val appWidgetId = appWidgetHost.allocateAppWidgetId(); widgetPickerLauncher.launch(Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)) }.padding(16.dp)); Divider(); Text("Duvar Kağıtları", modifier = Modifier.fillMaxWidth().clickable { showWorkspaceMenu = false; context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SET_WALLPAPER), "Duvar Kağıdı Seç")) }.padding(16.dp)); Divider(); Text("NuoL Ayarları", modifier = Modifier.fillMaxWidth().clickable { showWorkspaceMenu = false; onOpenSettings() }.padding(16.dp)) } }, confirmButton = { TextButton(onClick = { showWorkspaceMenu = false }) { Text("İptal") } })
        }
    }
}

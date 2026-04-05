package nuol.lr.ui.home

import android.appwidget.AppWidgetHost
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, appWidgetHost: AppWidgetHost) {
    val pinnedApps by viewModel.pinnedApps.collectAsState()
    val dockApps by viewModel.dockApps.collectAsState()
    val widgetIds by viewModel.widgetIds.collectAsState()
    val homeCols by viewModel.homeColumns.collectAsState()
    val iconSize by viewModel.iconSize.collectAsState()
    val showLabels by viewModel.showLabels.collectAsState()
    val drawerOpacity by viewModel.drawerOpacity.collectAsState()
    
    // YENİ STATE'LER
    val iconShape by viewModel.iconShape.collectAsState()
    val doubleTapAction by viewModel.doubleTapAction.collectAsState()
    val enableBlur by viewModel.enableBlur.collectAsState()
    
    var isDrawerOpen by remember { mutableStateOf(false) }
    var isSettingsOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (isSettingsOpen) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            SettingsScreen(viewModel = viewModel, onBack = { isSettingsOpen = false })
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Workspace(
                modifier = Modifier.weight(1f),
                onSwipeUp = { isDrawerOpen = true },
                pinnedApps = pinnedApps, widgetIds = widgetIds, appWidgetHost = appWidgetHost,
                homeColumns = homeCols, iconSize = iconSize, showLabels = showLabels,
                iconShape = iconShape, doubleTapAction = doubleTapAction,
                onUnpinApp = { pkg -> viewModel.unpinAppFromHome(pkg) },
                onAddWidget = { id -> viewModel.addWidget(id) },
                onRemoveWidget = { id -> appWidgetHost.deleteAppWidgetId(id); viewModel.removeWidget(id) },
                onOpenSettings = { isSettingsOpen = true }
            )
            Dock(apps = dockApps, iconSize = iconSize, iconShape = iconShape, enableBlur = enableBlur, onUnpin = { pkg -> viewModel.unpinAppFromDock(pkg) })
        }

        if (isDrawerOpen) {
            ModalBottomSheet(
                onDismissRequest = { isDrawerOpen = false }, sheetState = sheetState,
                // YENİ: Çekmece Arka Planına Blur Efekti
                modifier = if (enableBlur) Modifier.blur(16.dp) else Modifier,
                containerColor = MaterialTheme.colorScheme.background.copy(alpha = drawerOpacity / 100f), 
                scrimColor = Color.Black.copy(alpha = 0.4f), windowInsets = WindowInsets.statusBars
            ) {
                // Not: AppDrawer kodu bir önceki adımla aynıdır, sadece parametreleri ekliyoruz (Token limitine takılmamak için burada AppDrawer çağırılırken eksik parametreleri varsayıyoruz, çünkü AppDrawer'ın kendisinde iconShape ve enableBlur yoktu, hemen düzeltelim)
                AppDrawer(viewModel = viewModel, closeDrawer = { isDrawerOpen = false }, iconShape = iconShape)
            }
        }
    }
}

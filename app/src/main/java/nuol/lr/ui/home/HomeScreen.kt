package nuol.lr.ui.home

import android.appwidget.AppWidgetHost
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, appWidgetHost: AppWidgetHost) {
    val pinnedApps by viewModel.pinnedApps.collectAsState()
    val dockApps by viewModel.dockApps.collectAsState()
    val widgetIds by viewModel.widgetIds.collectAsState()
    val homeCols by viewModel.homeColumns.collectAsState()
    val iconSize by viewModel.iconSize.collectAsState()
    val showLabels by viewModel.showLabels.collectAsState()
    
    // YENİ: Saydamlık değerini dinliyoruz
    val drawerOpacity by viewModel.drawerOpacity.collectAsState()
    
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
                onUnpinApp = { pkg -> viewModel.unpinAppFromHome(pkg) },
                onAddWidget = { id -> viewModel.addWidget(id) },
                onRemoveWidget = { id -> appWidgetHost.deleteAppWidgetId(id); viewModel.removeWidget(id) },
                onOpenSettings = { isSettingsOpen = true }
            )
            Dock(apps = dockApps, iconSize = iconSize, onUnpin = { pkg -> viewModel.unpinAppFromDock(pkg) })
        }

        if (isDrawerOpen) {
            ModalBottomSheet(
                onDismissRequest = { isDrawerOpen = false }, sheetState = sheetState,
                // YENİ: Slider'dan gelen değere göre arka planın şeffaflığı belirleniyor
                containerColor = MaterialTheme.colorScheme.background.copy(alpha = drawerOpacity / 100f), 
                scrimColor = Color.Black.copy(alpha = 0.4f), windowInsets = WindowInsets.statusBars
            ) {
                AppDrawer(viewModel = viewModel, closeDrawer = { isDrawerOpen = false })
            }
        }
    }
}

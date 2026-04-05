package nuol.lr.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val pinnedApps by viewModel.pinnedApps.collectAsState()
    val dockApps by viewModel.dockApps.collectAsState()
    val iconPacks by viewModel.iconPacks.collectAsState()
    
    var isDrawerOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(modifier = Modifier.fillMaxSize()) {
        Workspace(
            modifier = Modifier.weight(1f),
            onSwipeUp = { isDrawerOpen = true },
            iconPacks = iconPacks,
            pinnedApps = pinnedApps,
            onApplyIconPack = { pkg -> viewModel.applyIconPack(pkg) },
            onUnpinApp = { pkg -> viewModel.unpinAppFromHome(pkg) }
        )
        Dock(apps = dockApps, onUnpin = { pkg -> viewModel.unpinAppFromDock(pkg) })
    }

    if (isDrawerOpen) {
        ModalBottomSheet(
            onDismissRequest = { isDrawerOpen = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
            scrimColor = Color.Black.copy(alpha = 0.4f),
            windowInsets = WindowInsets.statusBars
        ) {
            AppDrawer(viewModel = viewModel, closeDrawer = { isDrawerOpen = false })
        }
    }
}

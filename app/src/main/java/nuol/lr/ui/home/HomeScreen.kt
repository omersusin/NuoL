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
    val apps by viewModel.apps.collectAsState()
    val iconPacks by viewModel.iconPacks.collectAsState()
    
    var isDrawerOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(modifier = Modifier.fillMaxSize()) {
        Workspace(
            modifier = Modifier.weight(1f),
            onSwipeUp = { isDrawerOpen = true },
            iconPacks = iconPacks,
            onApplyIconPack = { packageName -> viewModel.applyIconPack(packageName) }
        )

        if (apps.isNotEmpty()) {
            Dock(apps = apps)
        }
    }

    if (isDrawerOpen) {
        ModalBottomSheet(
            onDismissRequest = { isDrawerOpen = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
            scrimColor = Color.Black.copy(alpha = 0.4f),
            windowInsets = WindowInsets.statusBars
        ) {
            AppDrawer(viewModel = viewModel)
        }
    }
}

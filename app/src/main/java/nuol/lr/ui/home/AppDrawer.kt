package nuol.lr.ui.home

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import nuol.lr.core.AppInfo

@Composable
fun AppDrawer(viewModel: HomeViewModel = viewModel()) {
    val apps by viewModel.apps.collectAsState()
    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(4), // Klasik başlatıcılar gibi 4 sütun
        contentPadding = WindowInsets.statusBars.asPaddingValues(), // Edge-to-edge (Tam ekran) desteği
        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        items(apps) { app ->
            AppItem(app = app) {
                // Tıklandığında uygulamayı başlat
                val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(launchIntent)
                }
            }
        }
    }
}

@Composable
fun AppItem(app: AppInfo, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(16.dp)) // Expressive tasarım: Büyük kavisler
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        // Coil kütüphanesi Android ikonlarını çok yüksek performansla çizer
        AsyncImage(
            model = app.icon,
            contentDescription = app.label,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

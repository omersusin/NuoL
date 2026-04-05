package nuol.lr.ui.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import nuol.lr.core.AppInfo

@Composable
fun Dock(apps: List<AppInfo>, iconSize: Int, modifier: Modifier = Modifier, onUnpin: (String) -> Unit) {
    val context = LocalContext.current
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp).heightIn(min = 80.dp).clip(RoundedCornerShape(32.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)).padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
    ) {
        apps.forEach { app ->
            var expanded by remember { mutableStateOf(false) }
            Box {
                AppItemUI(app = app, iconSize = iconSize, showLabel = false, // Dock'ta yazı olmaz
                    onClick = { context.packageManager.getLaunchIntentForPackage(app.packageName)?.let { context.startActivity(it) } },
                    onLongClick = { expanded = true }
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("Kaldır", color = MaterialTheme.colorScheme.error) }, onClick = { expanded = false; onUnpin(app.packageName) })
                }
            }
        }
    }
}

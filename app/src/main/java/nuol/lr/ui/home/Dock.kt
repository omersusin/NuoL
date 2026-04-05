package nuol.lr.ui.home

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import nuol.lr.core.AppInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Dock(apps: List<AppInfo>, modifier: Modifier = Modifier, onUnpin: (String) -> Unit) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .heightIn(min = 80.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (apps.isEmpty()) {
            Text("Uygulama eklemek için çekmeceden uzun basın", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            apps.forEach { app ->
                var expanded by remember { mutableStateOf(false) }
                Box {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .combinedClickable(
                                onClick = {
                                    val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                    intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)?.let { context.startActivity(it) }
                                },
                                onLongClick = { expanded = true }
                            )
                            .padding(8.dp)
                    ) {
                        AsyncImage(model = app.icon, contentDescription = app.label, modifier = Modifier.size(56.dp))
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Dock'tan Kaldır", color = MaterialTheme.colorScheme.error) },
                            onClick = { expanded = false; onUnpin(app.packageName) }
                        )
                    }
                }
            }
        }
    }
}

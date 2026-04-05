package nuol.lr.ui.home

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.*
import nuol.lr.core.AppInfo
import nuol.lr.core.IconPackInfo

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Workspace(
    modifier: Modifier = Modifier,
    onSwipeUp: () -> Unit,
    iconPacks: List<IconPackInfo>,
    pinnedApps: List<AppInfo>, // YENİ: Ana ekrandaki uygulamalar
    onApplyIconPack: (String?) -> Unit,
    onUnpinApp: (String) -> Unit // YENİ: Kaldırma işlemi
) {
    val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    val currentDate = dateFormat.format(Date())
    val context = LocalContext.current
    
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "Günaydın,"
        in 12..17 -> "İyi Günler,"
        in 18..22 -> "İyi Akşamlar,"
        else -> "İyi Geceler,"
    }

    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -20) onSwipeUp()
                }
            }
    ) {
        Column(modifier = Modifier.padding(top = 100.dp, start = 32.dp).align(Alignment.TopStart)) {
            Text(greeting, style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
            Text(currentDate, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        }

        IconButton(onClick = { showDialog = true }, modifier = Modifier.align(Alignment.TopEnd).padding(top = 100.dp, end = 32.dp)) {
            Icon(Icons.Default.Brush, contentDescription = "Tema Seç", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
        }

        // YENİ: Ana Ekrana Eklenen Uygulamaları Gösteren Grid Alanı
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth().align(Alignment.Center).padding(horizontal = 8.dp, vertical = 32.dp)
        ) {
            items(pinnedApps) { app ->
                var expanded by remember { mutableStateOf(false) }
                
                Box {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .combinedClickable(
                                onClick = {
                                    val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                    if (intent != null) {
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(intent)
                                    }
                                },
                                onLongClick = { expanded = true } // Uzun basınca kaldırma menüsü aç
                            )
                            .padding(8.dp)
                    ) {
                        AsyncImage(model = app.icon, contentDescription = app.label, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = app.label, style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary, maxLines = 1,
                            overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold
                        )
                    }

                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Ana Ekrandan Kaldır", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                expanded = false
                                onUnpinApp(app.packageName)
                            }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("İkon Paketi Seç") },
                text = {
                    Column {
                        Text("Sistem Varsayılanı", modifier = Modifier.fillMaxWidth().clickable { onApplyIconPack(null); showDialog = false }.padding(16.dp))
                        Divider()
                        iconPacks.forEach { pack ->
                            Text(pack.label, modifier = Modifier.fillMaxWidth().clickable { onApplyIconPack(pack.packageName); showDialog = false }.padding(16.dp))
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { showDialog = false }) { Text("Kapat") } }
            )
        }
    }
}

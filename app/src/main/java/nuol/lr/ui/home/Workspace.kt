package nuol.lr.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
import nuol.lr.core.IconPackInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Workspace(
    modifier: Modifier = Modifier,
    onSwipeUp: () -> Unit,
    iconPacks: List<IconPackInfo>,
    onApplyIconPack: (String?) -> Unit
) {
    val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    val currentDate = dateFormat.format(Date())
    
    // Günün saatine göre dinamik karşılama mesajı
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
        // Dinamik Karşılama ve Tarih Alanı
        Column(modifier = Modifier.padding(top = 100.dp, start = 32.dp).align(Alignment.TopStart)) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = currentDate,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        // İkon Paketi Seçici Butonu
        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 100.dp, end = 32.dp)
        ) {
            Icon(Icons.Default.Brush, contentDescription = "Tema Seç", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("İkon Paketi Seç") },
                text = {
                    Column {
                        Text(
                            text = "Sistem Varsayılanı",
                            modifier = Modifier.fillMaxWidth().clickable {
                                onApplyIconPack(null)
                                showDialog = false
                            }.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Divider()
                        iconPacks.forEach { pack ->
                            Text(
                                text = pack.label,
                                modifier = Modifier.fillMaxWidth().clickable {
                                    onApplyIconPack(pack.packageName)
                                    showDialog = false
                                }.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { showDialog = false }) { Text("Kapat") } }
            )
        }
    }
}

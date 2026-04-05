package nuol.lr.ui.home

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AndroidWidget(
    appWidgetHost: AppWidgetHost,
    appWidgetId: Int,
    onRemove: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = {}, // Widget'ın kendi tıklaması XML üzerinden işlenir
                onLongClick = { expanded = true }
            )
    ) {
        AndroidView(
            factory = { context ->
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
                appWidgetHost.createView(context, appWidgetId, appWidgetInfo).apply {
                    setAppWidget(appWidgetId, appWidgetInfo)
                }
            },
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Widget'ı Kaldır", color = MaterialTheme.colorScheme.error) },
                onClick = {
                    expanded = false
                    onRemove()
                }
            )
        }
    }
}

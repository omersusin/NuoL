package nuol.lr.ui.home

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Workspace(
    modifier: Modifier = Modifier,
    onSwipeUp: () -> Unit
) {
    // Günü ve tarihi formatla (Expressive tasarım için büyük göstereceğiz)
    val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    val currentDate = dateFormat.format(Date())

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    // Eğer yukarı doğru (negatif Y yönünde) kaydırma varsa çekmeceyi aç
                    if (dragAmount < -20) {
                        onSwipeUp()
                    }
                }
            },
        contentAlignment = Alignment.TopStart
    ) {
        Column(modifier = Modifier.padding(top = 100.dp, start = 32.dp)) {
            Text(
                text = "NuoL",
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = currentDate,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

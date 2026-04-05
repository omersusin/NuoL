package nuol.lr.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Expressive yapı: Daha büyük kavisler (Rounded) kullanıcıya daha samimi gelir
val Shapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(24.dp), // Uygulama ikonları/kartları için ideal
    large = RoundedCornerShape(32.dp)   // Çekmece (Drawer) ve büyük pencereler için
)

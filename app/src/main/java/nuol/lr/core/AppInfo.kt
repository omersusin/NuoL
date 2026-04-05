package nuol.lr.core

import android.graphics.drawable.Drawable

data class AppInfo(
    val label: String,
    val packageName: String,
    val componentName: String,
    val icon: Drawable
)

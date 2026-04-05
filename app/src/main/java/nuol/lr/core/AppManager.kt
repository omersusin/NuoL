package nuol.lr.core

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AppInfo(
    val label: String,
    val packageName: String,
    val componentName: String, // İkon paketi haritası için gerekli
    val icon: Drawable
)

class AppManager(private val context: Context, private val iconPackManager: IconPackManager) {
    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        
        resolveInfos.map { resolveInfo ->
            val pkg = resolveInfo.activityInfo.packageName
            val cls = resolveInfo.activityInfo.name
            val componentString = "ComponentInfo{$pkg/$cls}" // appfilter.xml standardı
            
            // Orijinal sistem ikonunu al
            val defaultIcon = resolveInfo.loadIcon(pm)
            // İkon paketi seçiliyse üzerinden geçir, yoksa orijinali ver
            val finalIcon = iconPackManager.loadIcon(componentString, defaultIcon)

            AppInfo(
                label = resolveInfo.loadLabel(pm).toString(),
                packageName = pkg,
                componentName = componentString,
                icon = finalIcon
            )
        }.sortedBy { it.label.lowercase() }
    }
}

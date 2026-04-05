package nuol.lr.core

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import org.xmlpull.v1.XmlPullParser

data class IconPackInfo(val label: String, val packageName: String)

class IconPackManager(private val context: Context) {
    private val pm = context.packageManager
    private var iconPackContext: Context? = null
    private var iconPackRes: Resources? = null
    private val iconMap = mutableMapOf<String, String>()

    // Cihazdaki İkon Paketlerini Bulur (Nova Launcher formatı %99 standarttır)
    fun getAvailableIconPacks(): List<IconPackInfo> {
        val intent = Intent("com.novalauncher.THEME")
        val packs = pm.queryIntentActivities(intent, 0)
        return packs.map {
            IconPackInfo(
                label = it.loadLabel(pm).toString(),
                packageName = it.activityInfo.packageName
            )
        }.sortedBy { it.label.lowercase() }
    }

    // Seçilen İkon Paketinin Haritasını (appfilter.xml) Çıkarır
    fun setIconPack(packageName: String) {
        iconMap.clear()
        try {
            iconPackContext = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY)
            iconPackRes = iconPackContext?.resources
            
            val resId = iconPackRes?.getIdentifier("appfilter", "xml", packageName)
            if (resId != null && resId != 0) {
                val parser = iconPackRes!!.getXml(resId)
                var eventType = parser.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG && parser.name == "item") {
                        val component = parser.getAttributeValue(null, "component")
                        val drawable = parser.getAttributeValue(null, "drawable")
                        if (component != null && drawable != null) {
                            iconMap[component] = drawable
                        }
                    }
                    eventType = parser.next()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            iconPackContext = null
        }
    }

    // Haritadan Özel İkonu Getirir
    fun loadIcon(componentName: String, fallback: Drawable): Drawable {
        if (iconPackContext == null || iconPackRes == null) return fallback
        
        val drawableName = iconMap[componentName] ?: return fallback
        val resId = iconPackRes!!.getIdentifier(drawableName, "drawable", iconPackContext!!.packageName)
        
        return if (resId != 0) {
            try {
                iconPackRes!!.getDrawable(resId, null)
            } catch (e: Exception) {
                fallback
            }
        } else {
            fallback
        }
    }
}

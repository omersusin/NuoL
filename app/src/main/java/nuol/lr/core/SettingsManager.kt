package nuol.lr.core

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {
    companion object {
        val ICON_PACK_KEY = stringPreferencesKey("icon_pack_package")
        val PINNED_APPS_KEY = stringSetPreferencesKey("pinned_apps")
        val DOCK_APPS_KEY = stringSetPreferencesKey("dock_apps")
        val DRAWER_COLUMNS_KEY = intPreferencesKey("drawer_columns")
        val HOME_COLUMNS_KEY = intPreferencesKey("home_columns")
        
        // YENİ EKLENENLER: MVP GEREKSİNİMLERİ
        val ICON_SIZE_KEY = intPreferencesKey("icon_size") // Varsayılan 56dp
        val SHOW_LABELS_KEY = booleanPreferencesKey("show_labels") // Varsayılan true
        val THEME_MODE_KEY = intPreferencesKey("theme_mode") // 0: Sistem, 1: Açık, 2: Koyu
    }

    val iconPackFlow: Flow<String?> = context.dataStore.data.map { it[ICON_PACK_KEY] }
    val pinnedAppsFlow: Flow<Set<String>> = context.dataStore.data.map { it[PINNED_APPS_KEY] ?: emptySet() }
    val dockAppsFlow: Flow<Set<String>> = context.dataStore.data.map { it[DOCK_APPS_KEY] ?: emptySet() }
    val drawerColumnsFlow: Flow<Int> = context.dataStore.data.map { it[DRAWER_COLUMNS_KEY] ?: 4 }
    val homeColumnsFlow: Flow<Int> = context.dataStore.data.map { it[HOME_COLUMNS_KEY] ?: 4 }
    
    val iconSizeFlow: Flow<Int> = context.dataStore.data.map { it[ICON_SIZE_KEY] ?: 56 }
    val showLabelsFlow: Flow<Boolean> = context.dataStore.data.map { it[SHOW_LABELS_KEY] ?: true }
    val themeModeFlow: Flow<Int> = context.dataStore.data.map { it[THEME_MODE_KEY] ?: 0 }

    suspend fun setIconPackPreference(packageName: String?) { context.dataStore.edit { p -> if (packageName != null) p[ICON_PACK_KEY] = packageName else p.remove(ICON_PACK_KEY) } }
    suspend fun addPinnedApp(pkg: String) { context.dataStore.edit { p -> p[PINNED_APPS_KEY] = (p[PINNED_APPS_KEY] ?: emptySet()) + pkg } }
    suspend fun removePinnedApp(pkg: String) { context.dataStore.edit { p -> p[PINNED_APPS_KEY] = (p[PINNED_APPS_KEY] ?: emptySet()) - pkg } }
    suspend fun addDockApp(pkg: String) { context.dataStore.edit { p -> p[DOCK_APPS_KEY] = (p[DOCK_APPS_KEY] ?: emptySet()) + pkg } }
    suspend fun removeDockApp(pkg: String) { context.dataStore.edit { p -> p[DOCK_APPS_KEY] = (p[DOCK_APPS_KEY] ?: emptySet()) - pkg } }
    suspend fun setDrawerColumns(cols: Int) { context.dataStore.edit { p -> p[DRAWER_COLUMNS_KEY] = cols } }
    suspend fun setHomeColumns(cols: Int) { context.dataStore.edit { p -> p[HOME_COLUMNS_KEY] = cols } }
    
    suspend fun setIconSize(size: Int) { context.dataStore.edit { p -> p[ICON_SIZE_KEY] = size } }
    suspend fun setShowLabels(show: Boolean) { context.dataStore.edit { p -> p[SHOW_LABELS_KEY] = show } }
    suspend fun setThemeMode(mode: Int) { context.dataStore.edit { p -> p[THEME_MODE_KEY] = mode } }
}

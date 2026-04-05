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
        val ICON_SIZE_KEY = intPreferencesKey("icon_size")
        val SHOW_LABELS_KEY = booleanPreferencesKey("show_labels")
        val THEME_MODE_KEY = intPreferencesKey("theme_mode")
        val WIDGET_IDS_KEY = stringSetPreferencesKey("widget_ids")
        val HIDDEN_APPS_KEY = stringSetPreferencesKey("hidden_apps")
        val SHOW_STATUS_BAR_KEY = booleanPreferencesKey("show_status_bar")
        val DRAWER_OPACITY_KEY = intPreferencesKey("drawer_opacity")
        val APP_SORT_MODE_KEY = intPreferencesKey("app_sort_mode")
        
        // YENİ: Melez Başlatıcı Özellikleri
        val DOUBLE_TAP_ACTION_KEY = intPreferencesKey("double_tap_action") // 0:Yok, 1:Ayarlar, 2:Çekmece, 3:Bildirimler
        val ICON_SHAPE_KEY = intPreferencesKey("icon_shape") // 0:Orijinal, 1:Daire, 2:YuvarlakKare, 3:Kare, 4:SuDamlası
        val ENABLE_BLUR_KEY = booleanPreferencesKey("enable_blur") // Cam bulanıklığı efekti
    }

    val iconPackFlow: Flow<String?> = context.dataStore.data.map { it[ICON_PACK_KEY] }
    val pinnedAppsFlow: Flow<Set<String>> = context.dataStore.data.map { it[PINNED_APPS_KEY] ?: emptySet() }
    val dockAppsFlow: Flow<Set<String>> = context.dataStore.data.map { it[DOCK_APPS_KEY] ?: emptySet() }
    val drawerColumnsFlow: Flow<Int> = context.dataStore.data.map { it[DRAWER_COLUMNS_KEY] ?: 4 }
    val homeColumnsFlow: Flow<Int> = context.dataStore.data.map { it[HOME_COLUMNS_KEY] ?: 4 }
    val iconSizeFlow: Flow<Int> = context.dataStore.data.map { it[ICON_SIZE_KEY] ?: 56 }
    val showLabelsFlow: Flow<Boolean> = context.dataStore.data.map { it[SHOW_LABELS_KEY] ?: true }
    val themeModeFlow: Flow<Int> = context.dataStore.data.map { it[THEME_MODE_KEY] ?: 0 }
    val widgetIdsFlow: Flow<List<Int>> = context.dataStore.data.map { prefs -> (prefs[WIDGET_IDS_KEY] ?: emptySet()).mapNotNull { it.toIntOrNull() } }
    val hiddenAppsFlow: Flow<Set<String>> = context.dataStore.data.map { it[HIDDEN_APPS_KEY] ?: emptySet() }
    val showStatusBarFlow: Flow<Boolean> = context.dataStore.data.map { it[SHOW_STATUS_BAR_KEY] ?: true }
    val drawerOpacityFlow: Flow<Int> = context.dataStore.data.map { it[DRAWER_OPACITY_KEY] ?: 95 }
    val appSortModeFlow: Flow<Int> = context.dataStore.data.map { it[APP_SORT_MODE_KEY] ?: 0 }

    // YENİ AKIŞLAR
    val doubleTapActionFlow: Flow<Int> = context.dataStore.data.map { it[DOUBLE_TAP_ACTION_KEY] ?: 0 }
    val iconShapeFlow: Flow<Int> = context.dataStore.data.map { it[ICON_SHAPE_KEY] ?: 0 }
    val enableBlurFlow: Flow<Boolean> = context.dataStore.data.map { it[ENABLE_BLUR_KEY] ?: false }

    suspend fun setIconPackPreference(pkg: String?) { context.dataStore.edit { p -> if (pkg != null) p[ICON_PACK_KEY] = pkg else p.remove(ICON_PACK_KEY) } }
    suspend fun addPinnedApp(pkg: String) { context.dataStore.edit { p -> p[PINNED_APPS_KEY] = (p[PINNED_APPS_KEY] ?: emptySet()) + pkg } }
    suspend fun removePinnedApp(pkg: String) { context.dataStore.edit { p -> p[PINNED_APPS_KEY] = (p[PINNED_APPS_KEY] ?: emptySet()) - pkg } }
    suspend fun addDockApp(pkg: String) { context.dataStore.edit { p -> p[DOCK_APPS_KEY] = (p[DOCK_APPS_KEY] ?: emptySet()) + pkg } }
    suspend fun removeDockApp(pkg: String) { context.dataStore.edit { p -> p[DOCK_APPS_KEY] = (p[DOCK_APPS_KEY] ?: emptySet()) - pkg } }
    suspend fun setDrawerColumns(cols: Int) { context.dataStore.edit { p -> p[DRAWER_COLUMNS_KEY] = cols } }
    suspend fun setHomeColumns(cols: Int) { context.dataStore.edit { p -> p[HOME_COLUMNS_KEY] = cols } }
    suspend fun setIconSize(size: Int) { context.dataStore.edit { p -> p[ICON_SIZE_KEY] = size } }
    suspend fun setShowLabels(show: Boolean) { context.dataStore.edit { p -> p[SHOW_LABELS_KEY] = show } }
    suspend fun setThemeMode(mode: Int) { context.dataStore.edit { p -> p[THEME_MODE_KEY] = mode } }
    suspend fun addWidgetId(id: Int) { context.dataStore.edit { p -> p[WIDGET_IDS_KEY] = (p[WIDGET_IDS_KEY] ?: emptySet()) + id.toString() } }
    suspend fun removeWidgetId(id: Int) { context.dataStore.edit { p -> p[WIDGET_IDS_KEY] = (p[WIDGET_IDS_KEY] ?: emptySet()) - id.toString() } }
    suspend fun addHiddenApp(pkg: String) { context.dataStore.edit { p -> p[HIDDEN_APPS_KEY] = (p[HIDDEN_APPS_KEY] ?: emptySet()) + pkg } }
    suspend fun removeHiddenApp(pkg: String) { context.dataStore.edit { p -> p[HIDDEN_APPS_KEY] = (p[HIDDEN_APPS_KEY] ?: emptySet()) - pkg } }
    suspend fun setShowStatusBar(show: Boolean) { context.dataStore.edit { p -> p[SHOW_STATUS_BAR_KEY] = show } }
    suspend fun setDrawerOpacity(opacity: Int) { context.dataStore.edit { p -> p[DRAWER_OPACITY_KEY] = opacity } }
    suspend fun setAppSortMode(mode: Int) { context.dataStore.edit { p -> p[APP_SORT_MODE_KEY] = mode } }

    // YENİ FONKSİYONLAR
    suspend fun setDoubleTapAction(action: Int) { context.dataStore.edit { p -> p[DOUBLE_TAP_ACTION_KEY] = action } }
    suspend fun setIconShape(shape: Int) { context.dataStore.edit { p -> p[ICON_SHAPE_KEY] = shape } }
    suspend fun setEnableBlur(enable: Boolean) { context.dataStore.edit { p -> p[ENABLE_BLUR_KEY] = enable } }
}

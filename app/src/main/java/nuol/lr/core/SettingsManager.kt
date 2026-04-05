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
        val THEME_MODE_KEY = intPreferencesKey("theme_mode") // 0:Sys, 1:Light, 2:Dark, 3:OLED Black
        val WIDGET_IDS_KEY = stringSetPreferencesKey("widget_ids")
        val HIDDEN_APPS_KEY = stringSetPreferencesKey("hidden_apps")
        val SHOW_STATUS_BAR_KEY = booleanPreferencesKey("show_status_bar")
        val DRAWER_OPACITY_KEY = intPreferencesKey("drawer_opacity")
        val APP_SORT_MODE_KEY = intPreferencesKey("app_sort_mode")
        val DOUBLE_TAP_ACTION_KEY = intPreferencesKey("double_tap_action")
        val ICON_SHAPE_KEY = intPreferencesKey("icon_shape")
        val ENABLE_BLUR_KEY = booleanPreferencesKey("enable_blur")
        val CUSTOM_APP_NAMES_KEY = stringSetPreferencesKey("custom_app_names")
        val USE_MATERIAL_YOU_KEY = booleanPreferencesKey("use_material_you")
        val BOTTOM_SEARCH_BAR_KEY = booleanPreferencesKey("bottom_search_bar")
        val HAPTIC_FEEDBACK_KEY = booleanPreferencesKey("haptic_feedback")
        
        // YENİ: Uygulama kullanım istatistikleri (PaketAdı=:=KullanımSayısı)
        val APP_USAGE_STATS_KEY = stringSetPreferencesKey("app_usage_stats")
    }

    val iconPackFlow = context.dataStore.data.map { it[ICON_PACK_KEY] }
    val pinnedAppsFlow = context.dataStore.data.map { it[PINNED_APPS_KEY] ?: emptySet() }
    val dockAppsFlow = context.dataStore.data.map { it[DOCK_APPS_KEY] ?: emptySet() }
    val drawerColumnsFlow = context.dataStore.data.map { it[DRAWER_COLUMNS_KEY] ?: 4 }
    val homeColumnsFlow = context.dataStore.data.map { it[HOME_COLUMNS_KEY] ?: 4 }
    val iconSizeFlow = context.dataStore.data.map { it[ICON_SIZE_KEY] ?: 56 }
    val showLabelsFlow = context.dataStore.data.map { it[SHOW_LABELS_KEY] ?: true }
    val themeModeFlow = context.dataStore.data.map { it[THEME_MODE_KEY] ?: 0 }
    val widgetIdsFlow = context.dataStore.data.map { prefs -> (prefs[WIDGET_IDS_KEY] ?: emptySet()).mapNotNull { it.toIntOrNull() } }
    val hiddenAppsFlow = context.dataStore.data.map { it[HIDDEN_APPS_KEY] ?: emptySet() }
    val showStatusBarFlow = context.dataStore.data.map { it[SHOW_STATUS_BAR_KEY] ?: true }
    val drawerOpacityFlow = context.dataStore.data.map { it[DRAWER_OPACITY_KEY] ?: 95 }
    val appSortModeFlow = context.dataStore.data.map { it[APP_SORT_MODE_KEY] ?: 0 }
    val doubleTapActionFlow = context.dataStore.data.map { it[DOUBLE_TAP_ACTION_KEY] ?: 0 }
    val iconShapeFlow = context.dataStore.data.map { it[ICON_SHAPE_KEY] ?: 0 }
    val enableBlurFlow = context.dataStore.data.map { it[ENABLE_BLUR_KEY] ?: false }
    val customAppNamesFlow = context.dataStore.data.map { prefs -> val set = prefs[CUSTOM_APP_NAMES_KEY] ?: emptySet(); set.associate { val parts = it.split("=:="); parts[0] to (parts.getOrNull(1) ?: "") } }
    val useMaterialYouFlow = context.dataStore.data.map { it[USE_MATERIAL_YOU_KEY] ?: true }
    val bottomSearchBarFlow = context.dataStore.data.map { it[BOTTOM_SEARCH_BAR_KEY] ?: false }
    val hapticFeedbackFlow = context.dataStore.data.map { it[HAPTIC_FEEDBACK_KEY] ?: true }
    
    // YENİ
    val appUsageStatsFlow: Flow<Map<String, Int>> = context.dataStore.data.map { prefs ->
        val set = prefs[APP_USAGE_STATS_KEY] ?: emptySet()
        set.associate { val parts = it.split("=:="); parts[0] to (parts.getOrNull(1)?.toIntOrNull() ?: 0) }
    }

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
    suspend fun setDoubleTapAction(action: Int) { context.dataStore.edit { p -> p[DOUBLE_TAP_ACTION_KEY] = action } }
    suspend fun setIconShape(shape: Int) { context.dataStore.edit { p -> p[ICON_SHAPE_KEY] = shape } }
    suspend fun setEnableBlur(enable: Boolean) { context.dataStore.edit { p -> p[ENABLE_BLUR_KEY] = enable } }
    suspend fun setCustomAppName(pkg: String, newName: String) { context.dataStore.edit { p -> val current = (p[CUSTOM_APP_NAMES_KEY] ?: emptySet()).filterNot { it.startsWith("$pkg=:=") }.toMutableSet(); if (newName.isNotBlank()) current.add("$pkg=:=$newName"); p[CUSTOM_APP_NAMES_KEY] = current } }
    suspend fun setUseMaterialYou(use: Boolean) { context.dataStore.edit { p -> p[USE_MATERIAL_YOU_KEY] = use } }
    suspend fun setBottomSearchBar(bottom: Boolean) { context.dataStore.edit { p -> p[BOTTOM_SEARCH_BAR_KEY] = bottom } }
    suspend fun setHapticFeedback(enable: Boolean) { context.dataStore.edit { p -> p[HAPTIC_FEEDBACK_KEY] = enable } }
    
    // YENİ FONKSİYON: Uygulama açılış sayısını artırır (Yapay Zeka Mantığı)
    suspend fun incrementAppUsage(pkg: String) {
        context.dataStore.edit { p ->
            val set = p[APP_USAGE_STATS_KEY] ?: emptySet()
            val map = set.associate { val parts = it.split("=:="); parts[0] to (parts.getOrNull(1)?.toIntOrNull() ?: 0) }.toMutableMap()
            map[pkg] = (map[pkg] ?: 0) + 1
            p[APP_USAGE_STATS_KEY] = map.map { "${it.key}=:=${it.value}" }.toSet()
        }
    }
}

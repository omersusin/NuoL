package nuol.lr.core

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {
    companion object {
        val ICON_PACK_KEY = stringPreferencesKey("icon_pack_package")
        val PINNED_APPS_KEY = stringSetPreferencesKey("pinned_apps")
        val DOCK_APPS_KEY = stringSetPreferencesKey("dock_apps")
    }

    val iconPackFlow: Flow<String?> = context.dataStore.data.map { it[ICON_PACK_KEY] }
    val pinnedAppsFlow: Flow<Set<String>> = context.dataStore.data.map { it[PINNED_APPS_KEY] ?: emptySet() }
    val dockAppsFlow: Flow<Set<String>> = context.dataStore.data.map { it[DOCK_APPS_KEY] ?: emptySet() }

    suspend fun setIconPackPreference(packageName: String?) {
        context.dataStore.edit { prefs -> if (packageName != null) prefs[ICON_PACK_KEY] = packageName else prefs.remove(ICON_PACK_KEY) }
    }

    suspend fun addPinnedApp(packageName: String) {
        context.dataStore.edit { prefs -> prefs[PINNED_APPS_KEY] = (prefs[PINNED_APPS_KEY] ?: emptySet()) + packageName }
    }

    suspend fun removePinnedApp(packageName: String) {
        context.dataStore.edit { prefs -> prefs[PINNED_APPS_KEY] = (prefs[PINNED_APPS_KEY] ?: emptySet()) - packageName }
    }

    suspend fun addDockApp(packageName: String) {
        context.dataStore.edit { prefs -> prefs[DOCK_APPS_KEY] = (prefs[DOCK_APPS_KEY] ?: emptySet()) + packageName }
    }

    suspend fun removeDockApp(packageName: String) {
        context.dataStore.edit { prefs -> prefs[DOCK_APPS_KEY] = (prefs[DOCK_APPS_KEY] ?: emptySet()) - packageName }
    }
}

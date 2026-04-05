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
        // YENİ: Ana ekrandaki uygulamaların paket adlarını tutan liste
        val PINNED_APPS_KEY = stringSetPreferencesKey("pinned_apps")
    }

    val iconPackFlow: Flow<String?> = context.dataStore.data.map { it[ICON_PACK_KEY] }
    
    // YENİ: Ana ekrandaki uygulamaları okuyan akış
    val pinnedAppsFlow: Flow<Set<String>> = context.dataStore.data.map { it[PINNED_APPS_KEY] ?: emptySet() }

    suspend fun setIconPackPreference(packageName: String?) {
        context.dataStore.edit { preferences ->
            if (packageName != null) preferences[ICON_PACK_KEY] = packageName
            else preferences.remove(ICON_PACK_KEY)
        }
    }

    // YENİ: Ana ekrana uygulama ekle
    suspend fun addPinnedApp(packageName: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[PINNED_APPS_KEY] ?: emptySet()
            preferences[PINNED_APPS_KEY] = current + packageName
        }
    }

    // YENİ: Ana ekrandan uygulama kaldır
    suspend fun removePinnedApp(packageName: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[PINNED_APPS_KEY] ?: emptySet()
            preferences[PINNED_APPS_KEY] = current - packageName
        }
    }
}

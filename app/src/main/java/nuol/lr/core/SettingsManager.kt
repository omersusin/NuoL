package nuol.lr.core

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Cihazda "settings.preferences_pb" adında güvenli bir dosya oluşturur
private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {
    
    companion object {
        // İkon Paketinin paket adını tutacağımız anahtar kelime
        val ICON_PACK_KEY = stringPreferencesKey("icon_pack_package")
    }

    // Seçili ikon paketini sürekli dinleyen bir akış (Flow)
    val iconPackFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[ICON_PACK_KEY]
        }

    // Kullanıcı yeni bir ikon paketi seçtiğinde bunu kaydeder
    suspend fun setIconPackPreference(packageName: String?) {
        context.dataStore.edit { preferences ->
            if (packageName != null) {
                preferences[ICON_PACK_KEY] = packageName
            } else {
                // Eğer Sistem Varsayılanı seçildiyse, hafızadan sileriz
                preferences.remove(ICON_PACK_KEY)
            }
        }
    }
}

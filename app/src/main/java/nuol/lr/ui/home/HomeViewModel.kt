package nuol.lr.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nuol.lr.core.AppInfo
import nuol.lr.core.AppManager
import nuol.lr.core.IconPackInfo
import nuol.lr.core.IconPackManager

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val iconPackManager = IconPackManager(application)
    private val appManager = AppManager(application, iconPackManager)
    
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _iconPacks = MutableStateFlow<List<IconPackInfo>>(emptyList())
    val iconPacks: StateFlow<List<IconPackInfo>> = _iconPacks.asStateFlow()

    init {
        loadIconPacks()
        loadApps() // İlk açılışta orijinal ikonlarla yükle
    }

    private fun loadIconPacks() {
        viewModelScope.launch {
            _iconPacks.value = iconPackManager.getAvailableIconPacks()
        }
    }

    fun applyIconPack(packageName: String?) {
        viewModelScope.launch {
            if (packageName != null) {
                iconPackManager.setIconPack(packageName)
            } else {
                // Null gelirse ikon paketini sıfırla (Orijinal ikonlara dön)
                iconPackManager.setIconPack("") 
            }
            loadApps() // İkonlar değiştiği için listeyi yenile
        }
    }

    private fun loadApps() {
        viewModelScope.launch {
            _apps.value = appManager.getInstalledApps()
        }
    }
}

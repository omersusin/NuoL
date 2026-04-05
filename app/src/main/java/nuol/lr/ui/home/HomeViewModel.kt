package nuol.lr.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import nuol.lr.core.AppInfo
import nuol.lr.core.AppManager
import nuol.lr.core.IconPackInfo
import nuol.lr.core.IconPackManager
import nuol.lr.core.SettingsManager

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val iconPackManager = IconPackManager(application)
    private val appManager = AppManager(application, iconPackManager)
    
    // YENİ: Ayar yöneticisini başlattık
    private val settingsManager = SettingsManager(application)
    
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredApps: StateFlow<List<AppInfo>> = combine(_apps, _searchQuery) { appList, query ->
        if (query.isBlank()) appList
        else appList.filter { it.label.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _iconPacks = MutableStateFlow<List<IconPackInfo>>(emptyList())
    val iconPacks: StateFlow<List<IconPackInfo>> = _iconPacks.asStateFlow()

    init {
        loadIconPacks()
        
        // YENİ: Uygulama açıldığında kayıtlı ikon paketini DataStore'dan dinler
        viewModelScope.launch {
            settingsManager.iconPackFlow.collect { savedPack ->
                if (savedPack != null) {
                    iconPackManager.setIconPack(savedPack)
                } else {
                    iconPackManager.setIconPack("")
                }
                // Seçim cihazdan okunduktan sonra uygulamaları yükle
                loadApps()
            }
        }
    }

    private fun loadIconPacks() {
        viewModelScope.launch {
            _iconPacks.value = iconPackManager.getAvailableIconPacks()
        }
    }

    // YENİ: Artık değişikliği sadece hafızaya yazıyoruz, geri kalanını Flow hallediyor
    fun applyIconPack(packageName: String?) {
        viewModelScope.launch {
            settingsManager.setIconPackPreference(packageName)
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun loadApps() {
        viewModelScope.launch {
            _apps.value = appManager.getInstalledApps()
        }
    }
}

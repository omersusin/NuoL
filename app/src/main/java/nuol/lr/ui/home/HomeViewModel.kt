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

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val iconPackManager = IconPackManager(application)
    private val appManager = AppManager(application, iconPackManager)
    
    // Tüm uygulamalar
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    // Arama Çubuğu Metni
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Arama metnine göre filtrelenmiş uygulamalar
    val filteredApps: StateFlow<List<AppInfo>> = combine(_apps, _searchQuery) { appList, query ->
        if (query.isBlank()) {
            appList
        } else {
            appList.filter { it.label.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _iconPacks = MutableStateFlow<List<IconPackInfo>>(emptyList())
    val iconPacks: StateFlow<List<IconPackInfo>> = _iconPacks.asStateFlow()

    init {
        loadIconPacks()
        loadApps()
    }

    private fun loadIconPacks() {
        viewModelScope.launch {
            _iconPacks.value = iconPackManager.getAvailableIconPacks()
        }
    }

    fun applyIconPack(packageName: String?) {
        viewModelScope.launch {
            if (packageName != null) iconPackManager.setIconPack(packageName)
            else iconPackManager.setIconPack("") 
            loadApps()
        }
    }

    // Arama metni değiştikçe tetiklenir
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun loadApps() {
        viewModelScope.launch {
            _apps.value = appManager.getInstalledApps()
        }
    }
}

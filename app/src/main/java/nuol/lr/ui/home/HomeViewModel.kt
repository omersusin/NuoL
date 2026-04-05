package nuol.lr.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import nuol.lr.core.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val iconPackManager = IconPackManager(application)
    private val appManager = AppManager(application, iconPackManager)
    private val settingsManager = SettingsManager(application)
    
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredApps: StateFlow<List<AppInfo>> = combine(_apps, _searchQuery) { appList, query ->
        if (query.isBlank()) appList else appList.filter { it.label.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pinnedApps: StateFlow<List<AppInfo>> = combine(_apps, settingsManager.pinnedAppsFlow) { appList, pinned ->
        appList.filter { pinned.contains(it.packageName) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dockApps: StateFlow<List<AppInfo>> = combine(_apps, settingsManager.dockAppsFlow) { appList, dock ->
        appList.filter { dock.contains(it.packageName) }.take(5) // Dock max 5 uygulama alır
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _iconPacks = MutableStateFlow<List<IconPackInfo>>(emptyList())
    val iconPacks: StateFlow<List<IconPackInfo>> = _iconPacks.asStateFlow()

    init {
        viewModelScope.launch { _iconPacks.value = iconPackManager.getAvailableIconPacks() }
        viewModelScope.launch {
            settingsManager.iconPackFlow.collect { savedPack ->
                if (savedPack != null) iconPackManager.setIconPack(savedPack) else iconPackManager.setIconPack("")
                _apps.value = appManager.getInstalledApps()
            }
        }
    }

    fun applyIconPack(packageName: String?) = viewModelScope.launch { settingsManager.setIconPackPreference(packageName) }
    fun pinAppToHome(packageName: String) = viewModelScope.launch { settingsManager.addPinnedApp(packageName) }
    fun unpinAppFromHome(packageName: String) = viewModelScope.launch { settingsManager.removePinnedApp(packageName) }
    fun pinAppToDock(packageName: String) = viewModelScope.launch { settingsManager.addDockApp(packageName) }
    fun unpinAppFromDock(packageName: String) = viewModelScope.launch { settingsManager.removeDockApp(packageName) }
    fun onSearchQueryChange(query: String) { _searchQuery.value = query }
}

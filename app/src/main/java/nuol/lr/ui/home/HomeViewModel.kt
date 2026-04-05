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

    val filteredApps = combine(_apps, _searchQuery) { list, q -> if (q.isBlank()) list else list.filter { it.label.contains(q, true) } }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val pinnedApps = combine(_apps, settingsManager.pinnedAppsFlow) { list, pinned -> list.filter { pinned.contains(it.packageName) } }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val dockApps = combine(_apps, settingsManager.dockAppsFlow) { list, dock -> list.filter { dock.contains(it.packageName) }.take(5) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val iconPacks = MutableStateFlow<List<IconPackInfo>>(emptyList())

    val drawerColumns = settingsManager.drawerColumnsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 4)
    val homeColumns = settingsManager.homeColumnsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 4)
    val iconSize = settingsManager.iconSizeFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 56)
    val showLabels = settingsManager.showLabelsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val themeMode = settingsManager.themeModeFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    // YENİ: Widget listesi
    val widgetIds = settingsManager.widgetIdsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch { iconPacks.value = iconPackManager.getAvailableIconPacks() }
        viewModelScope.launch {
            settingsManager.iconPackFlow.collect { savedPack ->
                iconPackManager.setIconPack(savedPack ?: "")
                _apps.value = appManager.getInstalledApps()
            }
        }
    }

    fun pinAppToHome(pkg: String) = viewModelScope.launch { settingsManager.addPinnedApp(pkg) }
    fun unpinAppFromHome(pkg: String) = viewModelScope.launch { settingsManager.removePinnedApp(pkg) }
    fun pinAppToDock(pkg: String) = viewModelScope.launch { settingsManager.addDockApp(pkg) }
    fun unpinAppFromDock(pkg: String) = viewModelScope.launch { settingsManager.removeDockApp(pkg) }
    fun setDrawerCols(cols: Int) = viewModelScope.launch { settingsManager.setDrawerColumns(cols) }
    fun setHomeCols(cols: Int) = viewModelScope.launch { settingsManager.setHomeColumns(cols) }
    fun applyIconPack(pkg: String?) = viewModelScope.launch { settingsManager.setIconPackPreference(pkg) }
    fun setIconSize(size: Int) = viewModelScope.launch { settingsManager.setIconSize(size) }
    fun setShowLabels(show: Boolean) = viewModelScope.launch { settingsManager.setShowLabels(show) }
    fun setThemeMode(mode: Int) = viewModelScope.launch { settingsManager.setThemeMode(mode) }
    fun onSearchQueryChange(query: String) { _searchQuery.value = query }
    
    // YENİ: Widget yönetimi
    fun addWidget(id: Int) = viewModelScope.launch { settingsManager.addWidgetId(id) }
    fun removeWidget(id: Int) = viewModelScope.launch { settingsManager.removeWidgetId(id) }
}

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

    val appSortMode = settingsManager.appSortModeFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val filteredApps = combine(_apps, _searchQuery, settingsManager.hiddenAppsFlow, appSortMode) { list, q, hidden, sortMode -> 
        val visibleApps = list.filter { !hidden.contains(it.packageName) }
        val searchedApps = if (q.isBlank()) visibleApps else visibleApps.filter { it.label.contains(q, true) }
        if (sortMode == 1) searchedApps.sortedByDescending { it.label.lowercase() } else searchedApps.sortedBy { it.label.lowercase() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAppsWithHiddenState = combine(_apps, settingsManager.hiddenAppsFlow) { list, hidden -> list.map { Pair(it, hidden.contains(it.packageName)) } }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val pinnedApps = combine(_apps, settingsManager.pinnedAppsFlow) { list, pinned -> list.filter { pinned.contains(it.packageName) } }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val dockApps = combine(_apps, settingsManager.dockAppsFlow) { list, dock -> list.filter { dock.contains(it.packageName) }.take(5) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val iconPacks = MutableStateFlow<List<IconPackInfo>>(emptyList())

    val drawerColumns = settingsManager.drawerColumnsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 4)
    val homeColumns = settingsManager.homeColumnsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 4)
    val iconSize = settingsManager.iconSizeFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 56)
    val showLabels = settingsManager.showLabelsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val themeMode = settingsManager.themeModeFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val widgetIds = settingsManager.widgetIdsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val showStatusBar = settingsManager.showStatusBarFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val drawerOpacity = settingsManager.drawerOpacityFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 95)
    
    // YENİ STATE'LER
    val doubleTapAction = settingsManager.doubleTapActionFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val iconShape = settingsManager.iconShapeFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val enableBlur = settingsManager.enableBlurFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

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
    fun addWidget(id: Int) = viewModelScope.launch { settingsManager.addWidgetId(id) }
    fun removeWidget(id: Int) = viewModelScope.launch { settingsManager.removeWidgetId(id) }
    fun hideApp(pkg: String) = viewModelScope.launch { settingsManager.addHiddenApp(pkg) }
    fun unhideApp(pkg: String) = viewModelScope.launch { settingsManager.removeHiddenApp(pkg) }
    fun setShowStatusBar(show: Boolean) = viewModelScope.launch { settingsManager.setShowStatusBar(show) }
    fun setDrawerOpacity(opacity: Int) = viewModelScope.launch { settingsManager.setDrawerOpacity(opacity) }
    fun setAppSortMode(mode: Int) = viewModelScope.launch { settingsManager.setAppSortMode(mode) }
    
    // YENİ
    fun setDoubleTapAction(action: Int) = viewModelScope.launch { settingsManager.setDoubleTapAction(action) }
    fun setIconShape(shape: Int) = viewModelScope.launch { settingsManager.setIconShape(shape) }
    fun setEnableBlur(enable: Boolean) = viewModelScope.launch { settingsManager.setEnableBlur(enable) }
}

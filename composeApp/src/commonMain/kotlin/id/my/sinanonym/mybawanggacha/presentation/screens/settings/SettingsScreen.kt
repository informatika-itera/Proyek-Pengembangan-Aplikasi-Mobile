package id.my.sinanonym.mybawanggacha.presentation.screens.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.sinanonym.mybawanggacha.presentation.components.MBGMainRailKey
import id.my.sinanonym.mybawanggacha.presentation.components.MBGRailBackButton
import id.my.sinanonym.mybawanggacha.presentation.components.MBGSideRailScaffold
import org.koin.compose.viewmodel.koinViewModel
import id.my.sinanonym.mybawanggacha.presentation.screens.settings.components.SettingsAboutSection
import id.my.sinanonym.mybawanggacha.presentation.screens.settings.components.SettingsApiSection
import id.my.sinanonym.mybawanggacha.presentation.screens.settings.components.SettingsColorSchemeSection
import id.my.sinanonym.mybawanggacha.presentation.screens.settings.components.SettingsMainMenu
import id.my.sinanonym.mybawanggacha.presentation.screens.settings.components.SettingsNetworkSection
import id.my.sinanonym.mybawanggacha.presentation.screens.settings.components.SettingsPaneHeader
import id.my.sinanonym.mybawanggacha.presentation.screens.settings.components.SettingsRequestUsageSection
import id.my.sinanonym.mybawanggacha.presentation.screens.settings.components.SettingsThemeSection

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToMyLibrary: () -> Unit,
    onNavigateToAnimeList: () -> Unit,
    onNavigateToMangaList: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToGacha: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val systemDarkTheme = isSystemInDarkTheme()
    val isDarkMode = uiState.themeMode.resolve(systemDarkTheme)

    var selectedPane by remember { mutableStateOf(SettingsPane.Main) }

    MBGSideRailScaffold(
        selectedRailKey = "",
        onRailItemClick = { key ->
            when (key) {
                MBGMainRailKey.Home -> onNavigateHome()
                MBGMainRailKey.Search -> onNavigateToSearch()
                MBGMainRailKey.MyLibrary -> onNavigateToMyLibrary()
                MBGMainRailKey.Gacha -> onNavigateToGacha()
                MBGMainRailKey.AnimeList -> onNavigateToAnimeList()
                MBGMainRailKey.MangaList -> onNavigateToMangaList()
            }
        },
        topAction = {
            MBGRailBackButton(
                onClick = {
                    if (selectedPane == SettingsPane.Main) {
                        onNavigateBack()
                    } else {
                        selectedPane = SettingsPane.Main
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 4.dp, top = 32.dp, end = 18.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedPane) {
                SettingsPane.Main -> {
                    SettingsMainMenu(
                        themeMode = uiState.themeMode,
                        isDarkMode = isDarkMode,
                        networkMode = uiState.networkMode,
                        appColorScheme = uiState.appColorScheme,
                        aiApiSettings = uiState.aiApiSettings,
                        requestUsage = uiState.requestUsage,
                        onPaneSelected = { selectedPane = it }
                    )
                }

                SettingsPane.Appearance -> {
                    SettingsPaneHeader(
                        title = "Appearance",
                        description = "Theme dan tampilan aplikasi."
                    )
                    SettingsThemeSection(
                        themeMode = uiState.themeMode,
                        isDarkMode = isDarkMode,
                        onThemeModeSelected = viewModel::setThemeMode
                    )

                    SettingsColorSchemeSection(
                        selected = uiState.appColorScheme,
                        isDarkMode = isDarkMode,
                        onSelected = viewModel::setAppColorScheme
                    )
                }

                SettingsPane.DataAccess -> {
                    SettingsPaneHeader(
                        title = "Data & Offline",
                        description = "Atur network mode dan fallback cache."
                    )
                    SettingsNetworkSection(
                        networkMode = uiState.networkMode,
                        onNetworkModeSelected = viewModel::setNetworkMode
                    )
                }

                SettingsPane.Api -> {
                    SettingsPaneHeader(
                        title = "AI API",
                        description = "Pilih model dan simpan token API untuk fitur AI."
                    )
                    SettingsApiSection(
                        settings = uiState.aiApiSettings,
                        onModelSelected = viewModel::setAiApiModel,
                        onPersonalitySelected = viewModel::setAiApiPersonality,
                        onTokenChange = viewModel::setAiApiToken
                    )
                }

                SettingsPane.RequestUsage -> {
                    SettingsPaneHeader(
                        title = "Request Usage",
                        description = "Pantau pemakaian request Jikan."
                    )
                    SettingsRequestUsageSection(requestUsage = uiState.requestUsage)
                }

                SettingsPane.About -> {
                    SettingsPaneHeader(
                        title = "About",
                        description = "Info aplikasi dan sumber data."
                    )
                    SettingsAboutSection(showTitle = false)
                }
            }
        }
    }
}


internal enum class SettingsPane {
    Main,
    Appearance,
    DataAccess,
    Api,
    RequestUsage,
    About
}
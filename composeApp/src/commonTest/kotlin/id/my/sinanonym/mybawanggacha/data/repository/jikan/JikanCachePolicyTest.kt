package id.my.sinanonym.mybawanggacha.data.repository.jikan

import id.my.sinanonym.mybawanggacha.domain.settings.model.NetworkMode
import id.my.sinanonym.mybawanggacha.domain.settings.model.ThemeMode
import id.my.sinanonym.mybawanggacha.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JikanCachePolicyTest {
    @Test
    fun allowsNetwork_whenAuto_shouldReturnTrue() = runTest {
        val repository = FakeSettingsRepository(networkModeValue = NetworkMode.Auto)
        val policy = SettingsJikanCachePolicy(repository)

        assertTrue(policy.allowsNetwork())
    }

    @Test
    fun allowsNetwork_whenOfflineOnly_shouldReturnFalse() = runTest {
        val repository = FakeSettingsRepository(networkModeValue = NetworkMode.OfflineOnly)
        val policy = SettingsJikanCachePolicy(repository)

        assertFalse(policy.allowsNetwork())
    }

    @Test
    fun alwaysOnlinePolicy_shouldNotBlockNetwork() = runTest {
        assertTrue(AlwaysOnlineJikanCachePolicy.allowsNetwork())
    }
}

private class FakeSettingsRepository(
    themeModeValue: ThemeMode = ThemeMode.System,
    networkModeValue: NetworkMode = NetworkMode.Auto
) : SettingsRepository {
    private val themeModeState = MutableStateFlow(themeModeValue)
    private val networkModeState = MutableStateFlow(networkModeValue)

    override val themeMode: Flow<ThemeMode> = themeModeState
    override val networkMode: Flow<NetworkMode> = networkModeState

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        themeModeState.value = themeMode
    }

    override suspend fun setNetworkMode(networkMode: NetworkMode) {
        networkModeState.value = networkMode
    }
}

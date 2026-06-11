package id.pusakakata.presentation.screens.settings

import id.pusakakata.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlin.test.*

class FakeSettingsRepository : SettingsRepository {
    private val _theme = MutableStateFlow(AppTheme.SYSTEM)
    override fun getTheme(): Flow<AppTheme> = _theme
    override suspend fun setTheme(theme: AppTheme) {
        _theme.value = theme
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeSettingsRepository
    private lateinit var viewModel: SettingsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeSettingsRepository()
        viewModel = SettingsViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSetTheme() = runTest {
        val job = backgroundScope.launch { viewModel.theme.collect {} }
        advanceUntilIdle()
        
        viewModel.setTheme(AppTheme.DARK)
        advanceUntilIdle()
        assertEquals(AppTheme.DARK, viewModel.theme.value)
        job.cancel()
    }
}

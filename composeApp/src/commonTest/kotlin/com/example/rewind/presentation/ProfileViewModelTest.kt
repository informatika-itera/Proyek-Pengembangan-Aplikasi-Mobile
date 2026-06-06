package com.example.rewind.presentation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.example.rewind.data.local.datastore.UserPreferences
import com.example.rewind.data.repository.FakeMovieRepository
import com.example.rewind.domain.usecase.GetAllMoviesUseCase
import com.example.rewind.domain.usecase.GetFavoriteMoviesUseCase
import com.example.rewind.presentation.screens.profile.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// 1. Trik Elegan: Membuat Fake DataStore, bukan Fake UserPreferences!
class FakeDataStore : DataStore<Preferences> {
    private val _data = MutableStateFlow(emptyPreferences())
    override val data: Flow<Preferences> = _data

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val newData = transform(_data.value)
        _data.value = newData
        return newData
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeRepository: FakeMovieRepository
    private lateinit var realUserPreferences: UserPreferences
    private lateinit var getAllMoviesUseCase: GetAllMoviesUseCase
    private lateinit var getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase
    private lateinit var viewModel: ProfileViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeRepository = FakeMovieRepository()

        // 2. Suntikkan Fake DataStore ke UserPreferences ASLI
        val fakeDataStore = FakeDataStore()
        realUserPreferences = UserPreferences(fakeDataStore)

        getAllMoviesUseCase = GetAllMoviesUseCase(fakeRepository)
        getFavoriteMoviesUseCase = GetFavoriteMoviesUseCase(fakeRepository)

        viewModel = ProfileViewModel(
            getAllMoviesUseCase,
            getFavoriteMoviesUseCase,
            realUserPreferences
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `startEdit should turn on isEditMode`() = runTest {
        viewModel.startEdit()
        assertTrue(viewModel.isEditMode.value)
    }

    @Test
    fun `saveEdit should update user preferences and turn off isEditMode`() = runTest {
        advanceUntilIdle()

        viewModel.startEdit()
        viewModel.editName.value = "Nisa Rewind"
        viewModel.editBio.value = "Film Enthusiast"

        viewModel.saveEdit()
        advanceUntilIdle()

        assertFalse(viewModel.isEditMode.value)
    }

    @Test
    fun `cancelEdit should turn off isEditMode and revert input values to state values`() = runTest {
        realUserPreferences.setUserName("Choirunnisa")
        realUserPreferences.setUserBio("Developer KMP")

        advanceUntilIdle()

        viewModel.startEdit()
        viewModel.editName.value = "Nama Ngaco"
        viewModel.editBio.value = "Bio Ngaco"

        // Batalkan edit
        viewModel.cancelEdit()

        assertFalse(viewModel.isEditMode.value)
        // Harus kembali ke nilai yang ada di dalam DataStore
        assertEquals("Choirunnisa", viewModel.editName.value)
        assertEquals("Developer KMP", viewModel.editBio.value)
    }
}
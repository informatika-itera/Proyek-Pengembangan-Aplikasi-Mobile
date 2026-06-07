package com.example.musickeep.presentation.screens.addmusic

import com.example.musickeep.domain.model.Music
import com.example.musickeep.domain.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FakeAddMusicRepository : MusicRepository {
    var insertCalled = false
    var updateCalled = false
    override fun getAllMusic(): Flow<List<Music>> = flowOf(emptyList())
    override fun searchMusic(query: String): Flow<List<Music>> = flowOf(emptyList())
    override suspend fun getMusicById(id: Long): Music? = Music(id = id, title = "Loaded", artist = "Artist")
    override suspend fun insertMusic(music: Music) { insertCalled = true }
    override suspend fun updateMusic(music: Music) { updateCalled = true }
    override suspend fun deleteMusic(id: Long) {}
    override fun getTotalCount(): Flow<Long> = flowOf(0L)
    override fun getMostCommonGenre(): Flow<String?> = flowOf(null)
}

@OptIn(ExperimentalCoroutinesApi::class)
class AddMusicViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeAddMusicRepository()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be empty`() {
        val viewModel = AddMusicViewModel(repository)
        val state = viewModel.uiState.value
        
        assertEquals("", state.title)
        assertEquals("", state.artist)
        assertEquals("", state.genre)
    }

    @Test
    fun `updating fields should update state`() {
        val viewModel = AddMusicViewModel(repository)
        viewModel.onTitleChange("Song")
        viewModel.onArtistChange("Artist")
        viewModel.onGenreSelect("Pop")
        
        val state = viewModel.uiState.value
        assertEquals("Song", state.title)
        assertEquals("Artist", state.artist)
        assertEquals("Pop", state.genre)
    }

    @Test
    fun `selecting Lainnya should enable custom genre`() {
        val viewModel = AddMusicViewModel(repository)
        viewModel.onGenreSelect("Lainnya")
        
        val state = viewModel.uiState.value
        assertEquals(true, state.isCustomGenre)
        assertEquals("", state.genre)
        
        viewModel.onCustomGenreChange("Indie")
        assertEquals("Indie", viewModel.uiState.value.genre)
    }

    @Test
    fun `saveMusic should call repository when fields are valid`() {
        val viewModel = AddMusicViewModel(repository)
        viewModel.onTitleChange("Title")
        viewModel.onArtistChange("Artist")
        
        viewModel.saveMusic()
        dispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(true, repository.insertCalled)
        assertEquals(true, viewModel.uiState.value.isSaved)
    }

    @Test
    fun `saveMusic should show error when title is blank`() {
        val viewModel = AddMusicViewModel(repository)
        viewModel.onArtistChange("Artist")
        
        viewModel.saveMusic()
        
        assertEquals(false, repository.insertCalled)
        assertEquals("Judul dan Artis wajib diisi!", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `loadMusic should update state with music data`() {
        val viewModel = AddMusicViewModel(repository)
        viewModel.loadMusic(1L)
        dispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals("Loaded", state.title)
        assertEquals("Artist", state.artist)
    }

    @Test
    fun `saveMusic should call update when id is present`() {
        val viewModel = AddMusicViewModel(repository)
        viewModel.loadMusic(1L)
        dispatcher.scheduler.advanceUntilIdle()
        
        viewModel.saveMusic()
        dispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(true, repository.updateCalled)
        assertEquals(false, repository.insertCalled)
    }
}

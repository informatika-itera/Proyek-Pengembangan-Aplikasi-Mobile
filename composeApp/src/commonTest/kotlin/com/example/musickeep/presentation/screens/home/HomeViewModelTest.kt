package com.example.musickeep.presentation.screens.home

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

class FakeHomeRepository : MusicRepository {
    var deleteCalled = false
    override fun getAllMusic(): Flow<List<Music>> = flowOf(listOf(Music(id = 1, title = "Song 1", artist = "Artist 1")))
    override fun searchMusic(query: String): Flow<List<Music>> = flowOf(
        listOf(Music(id = 1, title = "Song 1", artist = "Artist 1")).filter { it.title.contains(query) }
    )
    override suspend fun getMusicById(id: Long): Music? = null
    override suspend fun insertMusic(music: Music) {}
    override suspend fun updateMusic(music: Music) {}
    override suspend fun deleteMusic(id: Long) { deleteCalled = true }
    override fun getTotalCount(): Flow<Long> = flowOf(1L)
    override fun getMostCommonGenre(): Flow<String?> = flowOf("Pop")
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeHomeRepository()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load music`() {
        val viewModel = HomeViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(1, viewModel.uiState.value.musicList.size)
        assertEquals("Song 1", viewModel.uiState.value.musicList[0].title)
    }

    @Test
    fun `updating search query should update state`() {
        val viewModel = HomeViewModel(repository)
        viewModel.onSearchQueryChange("New Song")
        
        assertEquals("New Song", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun `selecting genre should update state`() {
        val viewModel = HomeViewModel(repository)
        viewModel.onGenreSelect("Rock")
        
        assertEquals("Rock", viewModel.uiState.value.selectedGenre)
    }

    @Test
    fun `deleting music should call repository`() {
        val viewModel = HomeViewModel(repository)
        viewModel.deleteMusic(1L)
        dispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(true, repository.deleteCalled)
    }

    @Test
    fun `searching for non-existent song should return empty list`() {
        val viewModel = HomeViewModel(repository)
        viewModel.onSearchQueryChange("Ghost")
        dispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(0, viewModel.uiState.value.musicList.size)
    }
}

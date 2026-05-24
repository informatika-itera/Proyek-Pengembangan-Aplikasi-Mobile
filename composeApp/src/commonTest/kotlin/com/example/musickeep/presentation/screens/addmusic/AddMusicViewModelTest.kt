package com.example.musickeep.presentation.screens.addmusic

import com.example.musickeep.domain.model.Music
import com.example.musickeep.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.test.Test
import kotlin.test.assertEquals

class FakeMusicRepository : MusicRepository {
    override fun getAllMusic(): Flow<List<Music>> = flowOf(emptyList())
    override fun searchMusic(query: String): Flow<List<Music>> = flowOf(emptyList())
    override suspend fun getMusicById(id: Long): Music? = null
    override suspend fun insertMusic(music: Music) {}
    override suspend fun updateMusic(music: Music) {}
    override suspend fun deleteMusic(id: Long) {}
}

class AddMusicViewModelTest {

    private val repository = FakeMusicRepository()

    @Test
    fun `initial state should be empty`() {
        val viewModel = AddMusicViewModel(repository)
        val state = viewModel.uiState.value
        
        assertEquals("", state.title)
        assertEquals("", state.artist)
        assertEquals("", state.genre)
    }

    @Test
    fun `updating title should update state`() {
        val viewModel = AddMusicViewModel(repository)
        viewModel.onTitleChange("Hype Boy")
        
        assertEquals("Hype Boy", viewModel.uiState.value.title)
    }
}

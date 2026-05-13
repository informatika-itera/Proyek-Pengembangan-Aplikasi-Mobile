package com.example.noteai.domain.usecase

import com.example.noteai.domain.model.Game

class GetGamesByGenre(
    private val games: List<Game> = emptyList()
) {
    operator fun invoke(genre: String): List<Game> {
        return games.filter { it.genre.contains(genre) }
    }
}
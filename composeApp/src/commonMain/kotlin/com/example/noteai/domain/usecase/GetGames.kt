package com.example.noteai.domain.usecase

import com.example.noteai.domain.model.Game

class GetGames(
    private val games: List<Game> = emptyList()
) {
    operator fun invoke(): List<Game> = games
}
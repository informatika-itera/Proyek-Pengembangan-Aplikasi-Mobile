package com.example.metaforge.data.local

import com.example.metaforge.domain.model.Hero
import com.example.metaforge.domain.model.HeroRole

object HeroDataSource {

    val allHeroes = listOf(
        // TANK
        Hero(1, "Khufra", HeroRole.TANK,
            "https://i.imgur.com/placeholder.png", "Crowd Control"),
        Hero(2, "Atlas", HeroRole.TANK,
            "https://i.imgur.com/placeholder.png", "Crowd Control"),
        Hero(3, "Lolita", HeroRole.TANK,
            "https://i.imgur.com/placeholder.png", "Guard"),
        Hero(4, "Edith", HeroRole.TANK,
            "https://i.imgur.com/placeholder.png", "Charge"),
        Hero(5, "Chip", HeroRole.TANK,
            "https://i.imgur.com/placeholder.png", "Initiator"),

        // FIGHTER
        Hero(6, "Chou", HeroRole.FIGHTER,
            "https://i.imgur.com/placeholder.png", "Control"),
        Hero(7, "Fredrinn", HeroRole.FIGHTER,
            "https://i.imgur.com/placeholder.png", "Charge"),
        Hero(8, "Aulus", HeroRole.FIGHTER,
            "https://i.imgur.com/placeholder.png", "Damage"),
        Hero(9, "Julian", HeroRole.FIGHTER,
            "https://i.imgur.com/placeholder.png", "Burst"),
        Hero(10, "Paquito", HeroRole.FIGHTER,
            "https://i.imgur.com/placeholder.png", "Control"),

        // ASSASSIN
        Hero(11, "Fanny", HeroRole.ASSASSIN,
            "https://i.imgur.com/placeholder.png", "Mobility"),
        Hero(12, "Ling", HeroRole.ASSASSIN,
            "https://i.imgur.com/placeholder.png", "Mobility"),
        Hero(13, "Gusion", HeroRole.ASSASSIN,
            "https://i.imgur.com/placeholder.png", "Burst"),
        Hero(14, "Joy", HeroRole.ASSASSIN,
            "https://i.imgur.com/placeholder.png", "Burst"),
        Hero(15, "Saber", HeroRole.ASSASSIN,
            "https://i.imgur.com/placeholder.png", "Burst"),

        // MAGE
        Hero(16, "Kagura", HeroRole.MAGE,
            "https://i.imgur.com/placeholder.png", "Burst"),
        Hero(17, "Novaria", HeroRole.MAGE,
            "https://i.imgur.com/placeholder.png", "Poke"),
        Hero(18, "Valentina", HeroRole.MAGE,
            "https://i.imgur.com/placeholder.png", "Burst"),
        Hero(19, "Xavier", HeroRole.MAGE,
            "https://i.imgur.com/placeholder.png", "Poke"),
        Hero(20, "Yve", HeroRole.MAGE,
            "https://i.imgur.com/placeholder.png", "Crowd Control"),

        // MARKSMAN
        Hero(21, "Beatrix", HeroRole.MARKSMAN,
            "https://i.imgur.com/placeholder.png", "Burst"),
        Hero(22, "Brody", HeroRole.MARKSMAN,
            "https://i.imgur.com/placeholder.png", "Burst"),
        Hero(23, "Natan", HeroRole.MARKSMAN,
            "https://i.imgur.com/placeholder.png", "Damage"),
        Hero(24, "Melissa", HeroRole.MARKSMAN,
            "https://i.imgur.com/placeholder.png", "Guard"),
        Hero(25, "Miya", HeroRole.MARKSMAN,
            "https://i.imgur.com/placeholder.png", "Damage"),

        // SUPPORT
        Hero(26, "Mathilda", HeroRole.SUPPORT,
            "https://i.imgur.com/placeholder.png", "Mobility"),
        Hero(27, "Floryn", HeroRole.SUPPORT,
            "https://i.imgur.com/placeholder.png", "Heal"),
        Hero(28, "Estes", HeroRole.SUPPORT,
            "https://i.imgur.com/placeholder.png", "Heal"),
        Hero(29, "Angela", HeroRole.SUPPORT,
            "https://i.imgur.com/placeholder.png", "Heal"),
        Hero(30, "Rafaela", HeroRole.SUPPORT,
            "https://i.imgur.com/placeholder.png", "Heal")
    )

    fun getByRole(role: HeroRole) = allHeroes.filter { it.role == role }
    fun search(query: String) = allHeroes.filter {
        it.name.contains(query, ignoreCase = true)
    }
    fun getById(id: Int) = allHeroes.find { it.id == id }
}
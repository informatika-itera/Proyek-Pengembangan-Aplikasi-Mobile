package com.example.metaforge.domain.model

enum class HeroRole(val displayName: String, val emoji: String) {
    TANK("Tank", "🛡️"),
    FIGHTER("Fighter", "⚔️"),
    ASSASSIN("Assassin", "🗡️"),
    MAGE("Mage", "🔮"),
    MARKSMAN("Marksman", "🏹"),
    SUPPORT("Support", "💚")
}
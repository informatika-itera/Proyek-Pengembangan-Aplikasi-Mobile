package com.example.hujjah.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    // ==================== HUJJAH ROUTES ====================

    @Serializable
    data object Home : Route // Beranda (Dashboard)

    @Serializable
    data object HujjahLens : Route // Lens (AI Counselor Chat)

    @Serializable
    data object Quran : Route // Qur'an list

    @Serializable
    data class QuranDetail(val surahNumber: Int, val surahName: String) : Route // Qur'an detail reading

    @Serializable
    data object Hadith : Route // Hadits grid/list

    @Serializable
    data object Profile : Route // Profil

    @Serializable
    data class HujjahResult(val topicId: String) : Route

    @Serializable
    data class ReferenceDetail(val referenceId: String) : Route

    @Serializable
    data object Bookmark : Route // Bookmark Khazanah
}

interface NavigationActions {
    fun navigateToHome()
    fun navigateToHujjahLens()
    fun navigateToQuran()
    fun navigateToQuranDetail(surahNumber: Int, surahName: String)
    fun navigateToHadith()
    fun navigateToProfile()
    fun navigateToHujjahResult(topicId: String)
    fun navigateToReferenceDetail(referenceId: String)
    fun navigateToBookmarks()
    fun navigateBack()
}

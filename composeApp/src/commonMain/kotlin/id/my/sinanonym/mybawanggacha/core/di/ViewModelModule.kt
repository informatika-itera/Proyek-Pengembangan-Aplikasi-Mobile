package id.my.sinanonym.mybawanggacha.core.di

import id.my.sinanonym.mybawanggacha.presentation.screens.notes.addnote.AddNoteViewModel
import id.my.sinanonym.mybawanggacha.presentation.screens.ai.AIAssistantViewModel
import id.my.sinanonym.mybawanggacha.presentation.screens.anime.detail.AnimeDetailViewModel
import id.my.sinanonym.mybawanggacha.presentation.screens.anime.home.AnimeHomeViewModel
import id.my.sinanonym.mybawanggacha.presentation.screens.anime.list.AnimeListViewModel
import id.my.sinanonym.mybawanggacha.presentation.screens.notes.detail.NoteDetailViewModel
import id.my.sinanonym.mybawanggacha.presentation.screens.discover.HomeViewModel
import id.my.sinanonym.mybawanggacha.presentation.screens.gacha.GachaViewModel
import id.my.sinanonym.mybawanggacha.presentation.screens.library.editor.LibraryEntryEditorViewModel
import id.my.sinanonym.mybawanggacha.presentation.screens.library.list.LibraryViewModel
import id.my.sinanonym.mybawanggacha.presentation.screens.manga.detail.MangaDetailViewModel
import id.my.sinanonym.mybawanggacha.presentation.screens.manga.list.MangaListViewModel
import id.my.sinanonym.mybawanggacha.presentation.screens.search.SearchViewModel
import id.my.sinanonym.mybawanggacha.presentation.screens.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddNoteViewModel)
    viewModelOf(::NoteDetailViewModel)
    viewModelOf(::AIAssistantViewModel)
    viewModelOf(::AnimeDetailViewModel)
    viewModelOf(::AnimeHomeViewModel)
    viewModelOf(::AnimeListViewModel)
    viewModelOf(::MangaDetailViewModel)
    viewModelOf(::MangaListViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::LibraryViewModel)
    viewModelOf(::LibraryEntryEditorViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::GachaViewModel)
}
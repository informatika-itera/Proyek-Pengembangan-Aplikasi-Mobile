package com.example.mybawanggacha.core.di

import com.example.mybawanggacha.presentation.screens.notes.addnote.AddNoteViewModel
import com.example.mybawanggacha.presentation.screens.ai.AIAssistantViewModel
import com.example.mybawanggacha.presentation.screens.anime.detail.AnimeDetailViewModel
import com.example.mybawanggacha.presentation.screens.anime.home.AnimeHomeViewModel
import com.example.mybawanggacha.presentation.screens.anime.list.AnimeListViewModel
import com.example.mybawanggacha.presentation.screens.notes.detail.NoteDetailViewModel
import com.example.mybawanggacha.presentation.screens.discover.HomeViewModel
import com.example.mybawanggacha.presentation.screens.library.editor.LibraryEntryEditorViewModel
import com.example.mybawanggacha.presentation.screens.library.list.LibraryViewModel
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
    viewModelOf(::LibraryViewModel)
    viewModelOf(::LibraryEntryEditorViewModel)
}

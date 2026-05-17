package com.example.mybawanggacha.core.di

import com.example.mybawanggacha.domain.note.usecase.DeleteNoteUseCase
import com.example.mybawanggacha.domain.note.usecase.GenerateIdeasUseCase
import com.example.mybawanggacha.domain.note.usecase.GetAllNotesUseCase
import com.example.mybawanggacha.domain.note.usecase.ImproveWritingUseCase
import com.example.mybawanggacha.domain.note.usecase.SaveNoteUseCase
import com.example.mybawanggacha.domain.note.usecase.SearchNotesUseCase
import com.example.mybawanggacha.domain.note.usecase.SummarizeNoteUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule = module {
    singleOf(::GetAllNotesUseCase)
    singleOf(::SearchNotesUseCase)
    singleOf(::SaveNoteUseCase)
    singleOf(::DeleteNoteUseCase)
    singleOf(::SummarizeNoteUseCase)
    singleOf(::ImproveWritingUseCase)
    singleOf(::GenerateIdeasUseCase)
}

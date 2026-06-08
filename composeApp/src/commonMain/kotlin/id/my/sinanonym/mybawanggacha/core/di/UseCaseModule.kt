package id.my.sinanonym.mybawanggacha.core.di

import id.my.sinanonym.mybawanggacha.domain.gacha.usecase.RunGachaUseCase
import id.my.sinanonym.mybawanggacha.domain.note.usecase.DeleteNoteUseCase
import id.my.sinanonym.mybawanggacha.domain.note.usecase.GenerateIdeasUseCase
import id.my.sinanonym.mybawanggacha.domain.note.usecase.GetAllNotesUseCase
import id.my.sinanonym.mybawanggacha.domain.note.usecase.ImproveWritingUseCase
import id.my.sinanonym.mybawanggacha.domain.note.usecase.SaveNoteUseCase
import id.my.sinanonym.mybawanggacha.domain.note.usecase.SearchNotesUseCase
import id.my.sinanonym.mybawanggacha.domain.note.usecase.SummarizeNoteUseCase
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
    singleOf(::RunGachaUseCase)
}

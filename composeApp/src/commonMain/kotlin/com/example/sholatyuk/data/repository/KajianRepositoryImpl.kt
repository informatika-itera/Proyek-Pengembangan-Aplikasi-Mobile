package com.example.sholatyuk.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.sholatyuk.data.local.KajianNoteEntity
import com.example.sholatyuk.data.local.SholatYukDatabase
import com.example.sholatyuk.domain.model.KajianCategory
import com.example.sholatyuk.domain.model.KajianNote
import com.example.sholatyuk.domain.repository.KajianRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class KajianRepositoryImpl(
    private val db: SholatYukDatabase
) : KajianRepository {

    private val queries = db.kajianNoteQueries

    override fun getAllNotes(): Flow<List<KajianNote>> {
        return queries.getAllNotes().asFlow().mapToList(Dispatchers.IO).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNotesByCategory(category: KajianCategory): Flow<List<KajianNote>> {
        return queries.getNotesByCategory(category.name).asFlow().mapToList(Dispatchers.IO).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchNotes(query: String): Flow<List<KajianNote>> {
        return queries.searchNotes(query, query).asFlow().mapToList(Dispatchers.IO).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertNote(note: KajianNote) {
        queries.insertNote(
            id = note.id,
            judul = note.judul,
            ustadz = note.ustadz,
            tanggal = note.tanggal,
            kategori = note.kategori.name,
            isi = note.isi
        )
    }

    override suspend fun deleteNote(id: String) {
        queries.deleteNote(id)
    }

    private fun KajianNoteEntity.toDomain(): KajianNote {
        return KajianNote(
            id = id,
            judul = judul,
            ustadz = ustadz,
            tanggal = tanggal,
            kategori = KajianCategory.valueOf(kategori),
            isi = isi
        )
    }
}
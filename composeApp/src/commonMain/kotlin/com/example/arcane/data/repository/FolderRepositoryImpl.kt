package com.example.arcane.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.arcane.data.local.ArcaneDatabase
import com.example.arcane.data.local.entity.toDomain
import com.example.arcane.domain.model.Book
import com.example.arcane.domain.model.Folder
import com.example.arcane.domain.repository.FolderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class FolderRepositoryImpl(
    database: ArcaneDatabase
) : FolderRepository {

    private val queries = database.folderQueries

    override fun getAllFolders(): Flow<List<Folder>> {
        return queries.getAllFolders()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.map { entity ->
                    Folder(
                        id = entity.id,
                        name = entity.name,
                        createdAt = entity.createdAt
                    )
                }
            }
    }

    override suspend fun getFolderById(id: Long): Folder? = withContext(Dispatchers.Default) {
        val entity = queries.getFolderById(id).executeAsOneOrNull()
        entity?.let {
            Folder(
                id = it.id,
                name = it.name,
                createdAt = it.createdAt
            )
        }
    }

    override suspend fun createFolder(name: String) = withContext(Dispatchers.IO) {
        queries.insertFolder(
            name = name,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
    }

    override suspend fun deleteFolder(id: Long) = withContext(Dispatchers.IO) {
        queries.deleteFolder(id)
    }

    override suspend fun updateFolderName(id: Long, newName: String) = withContext(Dispatchers.IO) {
        queries.updateFolderName(newName, id)
    }

    override suspend fun addBookToFolder(bookId: Long, folderId: Long) = withContext(Dispatchers.IO) {
        queries.addBookToFolder(bookId, folderId)
    }

    override suspend fun removeBookFromFolder(bookId: Long, folderId: Long) = withContext(Dispatchers.IO) {
        queries.removeBookFromFolder(bookId, folderId)
    }

    override fun getFoldersForBook(bookId: Long): Flow<List<Folder>> {
        return queries.getFoldersForBook(bookId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.map { entity ->
                    Folder(
                        id = entity.id,
                        name = entity.name,
                        createdAt = entity.createdAt
                    )
                }
            }
    }

    override fun getBooksInFolder(folderId: Long): Flow<List<Book>> {
        return queries.getBooksInFolder(folderId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }
}

package com.example.arcane.domain.repository

import com.example.arcane.domain.model.Book
import com.example.arcane.domain.model.Folder
import kotlinx.coroutines.flow.Flow

interface FolderRepository {
    fun getAllFolders(): Flow<List<Folder>>
    suspend fun getFolderById(id: Long): Folder?
    suspend fun createFolder(name: String)
    suspend fun deleteFolder(id: Long)
    suspend fun updateFolderName(id: Long, newName: String)
    
    suspend fun addBookToFolder(bookId: Long, folderId: Long)
    suspend fun removeBookFromFolder(bookId: Long, folderId: Long)
    
    fun getFoldersForBook(bookId: Long): Flow<List<Folder>>
    fun getBooksInFolder(folderId: Long): Flow<List<Book>>
}

package com.example.bookku.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.bookku.data.local.BookDatabase
import com.example.bookku.data.local.entity.toDomain
import com.example.bookku.domain.model.Comment
import com.example.bookku.domain.repository.CommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class CommentRepositoryImpl(private val database: BookDatabase) : CommentRepository {

    private val queries = database.commentQueries

    override fun getCommentsByBook(bookId: Long): Flow<List<Comment>> {
        return queries.getCommentsByBook(bookId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getReplies(parentCommentId: Long): Flow<List<Comment>> {
        return queries.getReplies(parentCommentId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun addComment(comment: Comment): Result<Long> = withContext(Dispatchers.Default) {
        try {
            queries.insertComment(
                book_id = comment.bookId,
                user_id = comment.userId,
                user_name = comment.userName,
                content = comment.content,
                parent_comment_id = comment.parentCommentId,
                created_at = comment.createdAt.toEpochMilliseconds(),
                updated_at = Clock.System.now().toEpochMilliseconds()
            )
            Result.success(comment.bookId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteComment(commentId: Long): Result<Unit> = withContext(Dispatchers.Default) {
        try {
            queries.deleteComment(commentId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun likeComment(commentId: Long): Result<Unit> = withContext(Dispatchers.Default) {
        try {
            queries.likeComment(commentId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAllComments(): Flow<List<Comment>> {
        return queries.getAllComments()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }
}

package com.example.bookku.domain.repository

import com.example.bookku.domain.model.Comment
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    fun getCommentsByBook(bookId: Long): Flow<List<Comment>>
    fun getReplies(parentCommentId: Long): Flow<List<Comment>>
    suspend fun addComment(comment: Comment): Result<Long>
    suspend fun deleteComment(commentId: Long): Result<Unit>
    suspend fun likeComment(commentId: Long): Result<Unit>
    fun getAllComments(): Flow<List<Comment>>
}

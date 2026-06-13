package com.example.bookku.domain.usecase

import com.example.bookku.domain.model.Comment
import com.example.bookku.domain.repository.CommentRepository

class AddCommentUseCase(private val commentRepository: CommentRepository) {
    suspend operator fun invoke(comment: Comment): Result<Long> {
        if (comment.content.isBlank()) return Result.failure(IllegalArgumentException("Comment tidak boleh kosong"))
        return commentRepository.addComment(comment)
    }
}

class DeleteCommentUseCase(private val commentRepository: CommentRepository) {
    suspend operator fun invoke(commentId: Long): Result<Unit> = commentRepository.deleteComment(commentId)
}

class LikeCommentUseCase(private val commentRepository: CommentRepository) {
    suspend operator fun invoke(commentId: Long): Result<Unit> = commentRepository.likeComment(commentId)
}

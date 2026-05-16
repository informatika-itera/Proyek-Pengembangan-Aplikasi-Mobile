package com.itera.news.domain.usecase

import com.itera.news.domain.model.Article
import com.itera.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class GetMbgNewsUseCase(private val repository: NewsRepository) {
    operator fun invoke(query: String? = null): Flow<Result<List<Article>>> {
        return repository.getMbgNews(query)
    }
}
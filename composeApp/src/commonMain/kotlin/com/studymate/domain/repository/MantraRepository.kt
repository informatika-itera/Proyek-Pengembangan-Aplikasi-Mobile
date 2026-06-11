package com.studymate.domain.repository

interface MantraRepository {
    suspend fun getRandomMantra(excludeMantra: String? = null): String
}

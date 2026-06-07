package com.studymate.data.repository

import com.studymate.data.local.StudyMateDatabase
import com.studymate.domain.repository.MantraRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class MantraRepositoryImpl(
    private val database: StudyMateDatabase
) : MantraRepository {
    private val queries = database.mantraQueries

    private val seedMantras = listOf(
        "Pendidikan adalah senjata paling ampuh untuk mengubah dunia.",
        "Belajar hari ini, memimpin esok hari.",
        "Kesuksesan bukanlah akhir, kegagalan bukanlah fatal: keberanian untuk melanjutkanlah yang penting.",
        "Akar dari pendidikan memang pahit, namun buahnya sangat manis.",
        "Jangan pernah berhenti belajar, karena hidup tidak pernah berhenti mengajar."
    )

    override suspend fun getRandomMantra(excludeMantra: String?): String {
        return withContext(Dispatchers.IO) {
            ensureSeeded()

            val selectedMantra = if (excludeMantra.isNullOrBlank()) {
                queries.selectRandomMantra().executeAsOne()
            } else {
                queries.selectRandomMantraExcluding(excludeMantra).executeAsOneOrNull()
                    ?: queries.selectRandomMantra().executeAsOne()
            }

            selectedMantra
        }
    }

    private fun ensureSeeded() {
        if (queries.countMantras().executeAsOne() > 0L) {
            return
        }

        seedMantras.forEach { mantra ->
            queries.insertMantra(mantra)
        }
    }
}

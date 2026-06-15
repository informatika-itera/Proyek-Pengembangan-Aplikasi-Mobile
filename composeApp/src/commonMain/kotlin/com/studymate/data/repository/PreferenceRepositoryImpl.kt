package com.studymate.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.studymate.data.local.SettingsEntity
import com.studymate.data.local.StudyMateDatabase
import com.studymate.domain.repository.PreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PreferenceRepositoryImpl(
    private val database: StudyMateDatabase
) : PreferenceRepository {
    private val queries = database.settingsQueries

    override fun isDarkTheme(): Flow<Boolean> {
        return queries.getSettings()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.isDarkMode != 0L }
    }

    override suspend fun setDarkTheme(isDark: Boolean) {
        withContext(Dispatchers.IO) {
            queries.upsertSettings(if (isDark) 1L else 0L)
        }
    }
}

package id.my.sinanonym.mybawanggacha.domain.settings.repository

import id.my.sinanonym.mybawanggacha.domain.settings.model.JikanServiceStatus
import kotlinx.coroutines.flow.Flow

interface JikanServiceStatusRepository {
    val status: Flow<JikanServiceStatus>
}

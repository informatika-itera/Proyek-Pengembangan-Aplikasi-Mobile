package id.my.sinanonym.mybawanggacha.domain.settings.repository

import id.my.sinanonym.mybawanggacha.domain.settings.model.JikanRequestUsage
import kotlinx.coroutines.flow.Flow

interface JikanRequestUsageRepository {
    val usage: Flow<JikanRequestUsage>
}

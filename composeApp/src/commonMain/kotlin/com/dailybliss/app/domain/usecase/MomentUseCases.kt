package com.dailybliss.app.domain.usecase

import com.dailybliss.app.domain.model.Moment
import com.dailybliss.app.domain.repository.MomentRepository
import kotlinx.coroutines.flow.Flow

enum class MomentSortBy(val displayName: String) {
    TITLE_ASC("Judul (A-Z)"),
    TITLE_DESC("Judul (Z-A)"),
    CREATED_ASC("Dibuat (Lama)"),
    CREATED_DESC("Dibuat (Baru)"),
    UPDATED_ASC("Diupdate (Lama)"),
    UPDATED_DESC("Diupdate (Baru)")
}

class GetAllMomentsUseCase(private val repository: MomentRepository) {
    operator fun invoke(): Flow<List<Moment>> = repository.getAllMoments()
}

class SearchMomentsUseCase(private val repository: MomentRepository) {
    operator fun invoke(query: String): Flow<List<Moment>> = repository.searchMoments(query)
}

class SaveMomentUseCase(private val repository: MomentRepository) {
    suspend operator fun invoke(moment: Moment): Long {
        return if (moment.id == 0L) {
            repository.insertMoment(moment)
        } else {
            repository.updateMoment(moment)
            moment.id
        }
    }
}

class DeleteMomentUseCase(private val repository: MomentRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteMoment(id)
}

class GetMomentByIdUseCase(private val repository: MomentRepository) {
    operator fun invoke(id: Long): Flow<Moment?> = repository.getMomentById(id)
}


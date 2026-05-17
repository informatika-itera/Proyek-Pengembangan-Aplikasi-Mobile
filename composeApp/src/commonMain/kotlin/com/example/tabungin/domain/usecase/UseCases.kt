package com.example.tabungin.domain.usecase

import com.example.tabungin.domain.model.Setoran
import com.example.tabungin.domain.model.Target
import com.example.tabungin.domain.repository.AIRepository
import com.example.tabungin.domain.repository.TargetRepository
import com.example.tabungin.domain.repository.WritingStyle
import kotlinx.coroutines.flow.Flow


class GetAllTargetsUseCase(private val repo: TargetRepository) {
    operator fun invoke(): Flow<List<Target>> = repo.getAllTargets()
}

class GetTargetByIdUseCase(private val repo: TargetRepository) {
    operator fun invoke(id: Long): Flow<Target?> = repo.getTargetById(id)
}

class InsertTargetUseCase(private val repo: TargetRepository) {
    suspend operator fun invoke(target: Target): Long {
        require(target.nama.isNotBlank()) { "Nama target tidak boleh kosong" }
        require(target.targetAmount > 0) { "Jumlah target harus lebih dari 0" }
        require(target.deadline.isNotBlank()) { "Deadline tidak boleh kosong" }
        return repo.insertTarget(target)
    }
}

class UpdateTargetUseCase(private val repo: TargetRepository) {
    suspend operator fun invoke(target: Target) {
        require(target.id > 0) { "Target ID tidak valid" }
        require(target.nama.isNotBlank()) { "Nama target tidak boleh kosong" }
        require(target.targetAmount > 0) { "Jumlah target harus lebih dari 0" }
        repo.updateTarget(target)
    }
}

class DeleteTargetUseCase(private val repo: TargetRepository) {
    suspend operator fun invoke(id: Long) = repo.deleteTarget(id)
}


class GetSetoranByTargetUseCase(private val repo: TargetRepository) {
    operator fun invoke(targetId: Long): Flow<List<Setoran>> =
        repo.getSetoranByTarget(targetId)
}

class GetAllSetoranUseCase(private val repo: TargetRepository) {
    operator fun invoke(): Flow<List<Setoran>> = repo.getAllSetoran()
}

class InsertSetoranUseCase(private val repo: TargetRepository) {
    suspend operator fun invoke(setoran: Setoran) {
        require(setoran.amount > 0) { "Jumlah setoran harus lebih dari 0" }
        require(setoran.tanggal.isNotBlank()) { "Tanggal tidak boleh kosong" }
        repo.insertSetoran(setoran)
    }
}

class DeleteSetoranUseCase(private val repo: TargetRepository) {
    suspend operator fun invoke(id: Long) = repo.deleteSetoran(id)
}

class SummarizeNoteUseCase(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(content: String): Result<String> {
        if (content.length < 50) {
            return Result.failure(IllegalArgumentException("Konten terlalu pendek untuk diringkas"))
        }
        return aiRepository.summarize(content)
    }
}

class ImproveWritingUseCase(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(content: String, style: WritingStyle = WritingStyle.NEUTRAL): Result<String> {
        if (content.isBlank()) {
            return Result.failure(IllegalArgumentException("Konten tidak boleh kosong"))
        }
        return aiRepository.improveWriting(content, style)
    }
}

class GenerateIdeasUseCase(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(topic: String): Result<List<String>> {
        if (topic.isBlank()) {
            return Result.failure(IllegalArgumentException("Topik tidak boleh kosong"))
        }
        return aiRepository.generateIdeas(topic)
    }
}

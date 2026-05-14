package com.example.pocketguard.domain.usecase

import com.example.pocketguard.domain.model.Transaction
import com.example.pocketguard.domain.model.TransactionCategory
import com.example.pocketguard.domain.repository.TransactionRepository
import com.example.pocketguard.domain.repository.AIRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// --- USE CASE: GET ALL ---
class GetAllTransactionsUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(sortBy: TransactionSortBy = TransactionSortBy.DATE_DESC): Flow<List<Transaction>> {
        return repository.getAllTransactions().map { transactions ->
            when (sortBy) {
                TransactionSortBy.DATE_ASC -> transactions.sortedBy { it.createdAt }
                TransactionSortBy.DATE_DESC -> transactions.sortedByDescending { it.createdAt }
                TransactionSortBy.AMOUNT_ASC -> transactions.sortedBy { it.amount }
                TransactionSortBy.AMOUNT_DESC -> transactions.sortedByDescending { it.amount }
                TransactionSortBy.CATEGORY -> transactions.sortedBy { it.category.name }
            }
        }
    }
}

enum class TransactionSortBy(val displayName: String) {
    DATE_ASC("Tanggal (Lama)"),
    DATE_DESC("Tanggal (Baru)"),
    AMOUNT_ASC("Nominal (Terkecil)"),
    AMOUNT_DESC("Nominal (Terbesar)"),
    CATEGORY("Kategori")
}

// --- USE CASE: SAVE (CREATE/UPDATE) ---
class SaveTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Long> {
        return try {
            if (transaction.amount <= 0) {
                return Result.failure(IllegalArgumentException("Nominal harus lebih dari 0"))
            }
            if (transaction.description.isBlank()) {
                return Result.failure(IllegalArgumentException("Deskripsi tidak boleh kosong"))
            }

            val id = repository.insertTransaction(transaction)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// --- USE CASE: DELETE ---
class DeleteTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return try {
            repository.deleteTransaction(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// --- USE CASE: AI ANALYZER (Pengganti Summarize) ---
class AnalyzeFinanceUseCase(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(transactions: List<Transaction>): Result<String> {
        if (transactions.isEmpty()) {
            return Result.failure(IllegalArgumentException("Belum ada data transaksi untuk dianalisis"))
        }

        // Mengubah list transaksi menjadi teks sederhana untuk dikirim ke AI
        val prompt = transactions.joinToString("\n") {
            "${it.type}: ${it.amount} (${it.category.displayName}) - ${it.description}"
        }

        return aiRepository.summarize(prompt)
    }
}
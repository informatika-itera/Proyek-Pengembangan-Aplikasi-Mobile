package com.studyhub.domain.model

sealed class AiError(message: String? = null) : Exception(message) {
    class NoInternet : AiError("Tidak ada koneksi internet")
    class QuotaExceeded : AiError("Batas penggunaan hari ini tercapai")
    class ApiError(val code: Int, message: String) : AiError(message)
    class ParseError(message: String) : AiError(message)
}

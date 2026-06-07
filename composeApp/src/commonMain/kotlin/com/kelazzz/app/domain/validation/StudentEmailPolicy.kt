package com.kelazzz.app.domain.validation

object StudentEmailPolicy {
    const val EMAIL_SUFFIX = "@student.itera.ac.id"

    fun acceptsLoginInput(input: String): Boolean {
        val normalized = input.trim().lowercase()
        return normalized.isNotBlank() &&
            (!normalized.contains("@") || isStudentEmail(normalized))
    }

    fun normalizeLoginInput(input: String): String {
        val normalized = input.trim().lowercase()
        if (normalized.isBlank()) return normalized

        return if (normalized.contains("@")) {
            normalized
        } else {
            "$normalized$EMAIL_SUFFIX"
        }
    }

    fun isStudentEmail(email: String): Boolean {
        val normalized = email.trim().lowercase()
        return normalized.length > EMAIL_SUFFIX.length && normalized.endsWith(EMAIL_SUFFIX)
    }
}

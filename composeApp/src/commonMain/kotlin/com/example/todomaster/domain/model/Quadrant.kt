package com.example.todomaster.domain.model

enum class Quadrant(val value: Long) {
    DO_FIRST(1L),   // Penting & Genting
    SCHEDULE(2L),   // Penting & Tak Genting
    DELEGATE(3L),   // Tak Penting & Genting
    DONT_DO(4L);    // Tak Penting & Tak Genting

    companion object {
        fun fromValue(value: Long): Quadrant {
            return entries.find { it.value == value } ?: DO_FIRST
        }
    }
}
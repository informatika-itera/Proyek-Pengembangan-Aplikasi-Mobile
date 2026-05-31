package com.studyhub.core.util

import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

// ==================== DATE/TIME EXTENSIONS ====================

fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()

fun LocalDate.atStartOfDayMillis(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    return this.atStartOfDayIn(timeZone).toEpochMilliseconds()
}

fun LocalDate.atEndOfDayMillis(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    return this.atStartOfDayIn(timeZone)
        .toEpochMilliseconds() + 86_399_999L
}

fun Long.toLocalDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone).date
}

fun Long.toLocalMillisFromUtc(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    // DatePicker returns UTC millis. We convert it to LocalDate in UTC, 
    // then back to start of day millis in our local timezone.
    val utcDate = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date
    return utcDate.atStartOfDayMillis(timeZone)
}

fun Instant.formatToDisplay(): String {
    val dateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dateTime.dayOfMonth}/${dateTime.monthNumber}/${dateTime.year} " +
            "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
}

fun Instant.formatDateOnly(): String {
    val dateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dateTime.dayOfMonth}/${dateTime.monthNumber}/${dateTime.year}"
}

fun Instant.formatTimeOnly(): String {
    val dateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
}

// ==================== STRING EXTENSIONS ====================

fun String.truncate(maxLength: Int): String {
    return if (this.length > maxLength) {
        this.take(maxLength - 3) + "..."
    } else {
        this
    }
}

fun String.capitalizeFirst(): String {
    return this.replaceFirstChar { it.uppercase() }
}

// ==================== RETRY HELPER ====================

suspend fun <T> retryWithBackoff(
    times: Int = 3,
    initialDelay: Long = 100,
    maxDelay: Long = 1000,
    factor: Double = 2.0,
    block: suspend () -> T
): T {
    var currentDelay = initialDelay
    repeat(times - 1) {
        try {
            return block()
        } catch (e: Exception) {
            println("Retry attempt failed: ${e.message}")
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }
    return block()
}

// ==================== RESULT EXTENSIONS ====================

inline fun <T, R> Result<T>.mapSuccess(transform: (T) -> R): Result<R> {
    return this.map(transform)
}

inline fun <T> Result<T>.handle(
    onSuccess: (T) -> Unit,
    onFailure: (Throwable) -> Unit
) {
    this.onSuccess(onSuccess)
    this.onFailure(onFailure)
}


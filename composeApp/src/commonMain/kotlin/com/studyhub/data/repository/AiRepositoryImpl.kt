package com.studyhub.data.repository

import com.studyhub.core.util.CacheKeyGenerator
import com.studyhub.data.local.AiCacheDataSource
import com.studyhub.data.local.AiCacheTTL
import com.studyhub.data.local.AiUsageLimit
import com.studyhub.data.remote.GroqApiClient
import com.studyhub.domain.model.*
import com.studyhub.domain.repository.AiRepository
import kotlinx.serialization.json.Json
import okio.IOException

class AiRepositoryImpl(
    private val groqApiClient: GroqApiClient,
    private val cacheDataSource: AiCacheDataSource
) : AiRepository {

    override suspend fun getSmartPriority(
        tasks: List<Task>
    ): List<PriorityResult> {

        // Guard: tidak perlu AI untuk < 2 tugas
        if (tasks.size < AiUsageLimit.MIN_TASKS_FOR_AI) {
            return tasks.mapIndexed { i, t ->
                PriorityResult(t.id, i + 1, "Diurutkan by deadline")
            }
        }

        // Layer 1: Cek cache
        val cacheKey = CacheKeyGenerator.forPriority(tasks)
        val cached = cacheDataSource.getValidCache(cacheKey)
        if (cached != null) {
            return parsePriorityResponse(cached.result, tasks)
        }

        // Layer 2: Cek quota harian
        if (!cacheDataSource.canCallPriority()) {
            return fallbackPrioritySort(tasks)
        }

        // Layer 3: Panggil AI dengan prompt terkompresi
        return try {
            val summaries = tasks.map { it.toAiSummary() }
            val prompt = buildPriorityPrompt(summaries)
            val response = groqApiClient.getChatCompletion(prompt)

            // Simpan ke cache TTL 6 jam
            cacheDataSource.insertCache(
                cacheKey = cacheKey,
                result = response,
                promptType = "priority",
                expiresAt = AiCacheTTL.priorityExpiry()
            )
            cacheDataSource.incrementPriorityUsage()
            cacheDataSource.deleteExpiredCache()

            parsePriorityResponse(response, tasks)
        } catch (e: IOException) {
            throw AiError.NoInternet()
        } catch (e: Exception) {
            if (e is AiError) throw e
            throw AiError.ApiError(-1, e.message ?: "Layanan AI sedang tidak tersedia")
        }
    }

    override suspend fun getSmartReminder(
        task: Task,
        completedHistory: List<Task>
    ): ReminderSchedule {

        // Layer 1: Cek cache
        val cacheKey = CacheKeyGenerator.forReminder(
            task, completedHistory.size
        )
        val cached = cacheDataSource.getValidCache(cacheKey)
        if (cached != null) {
            return parseReminderResponse(cached.result, task)
        }

        // Layer 2: Cek quota
        if (!cacheDataSource.canCallReminder()) {
            return fallbackReminder(task)
        }

        // Layer 3: Panggil AI
        return try {
            val prompt = buildReminderPrompt(task, completedHistory)
            val response = groqApiClient.getChatCompletion(prompt)

            cacheDataSource.insertCache(
                cacheKey = cacheKey,
                result = response,
                promptType = "reminder",
                expiresAt = AiCacheTTL.reminderExpiry()
            )
            cacheDataSource.incrementReminderUsage()

            parseReminderResponse(response, task)
        } catch (e: IOException) {
            throw AiError.NoInternet()
        } catch (e: Exception) {
            if (e is AiError) throw e
            throw AiError.ApiError(-1, e.message ?: "Layanan AI sedang tidak tersedia")
        }
    }

    override suspend fun getAiUsageStats(): AiUsageStats =
        cacheDataSource.getUsageStats()

    override suspend fun clearPriorityCache() {
        cacheDataSource.deleteExpiredCache()
    }

    // ── Prompt Builders (terkompresi) ──

    private fun buildPriorityPrompt(
        summaries: List<AiTaskSummary>
    ): String {
        val tasksJson = summaries.joinToString(",") {
            """{"id":"${it.i}","t":"${it.t}","s":"${it.s}",""" +
            """"p":"${it.p}","due":${it.due},"est":${it.est}}"""
        }
        return """Rank tasks by study urgency. Return JSON array only.
Format:[{"taskId":"...","priorityOrder":1,"reason":"...max 8 words"}]
Tasks:[$tasksJson]
Note: Use the exact value from the "id" field as the taskId in your response."""
    }

    private fun buildReminderPrompt(
        task: Task,
        history: List<Task>
    ): String {
        val summary = task.toAiSummary()
        val avgHoursBefore = if (history.isNotEmpty()) {
            history.filter { it.completedAt != null }
                .map { t ->
                    (t.dueDate - (t.completedAt ?: t.dueDate)) /
                    3_600_000
                }
                .average()
                .let { if (it.isNaN()) 24.0 else it }
        } else 24.0

        return """Suggest reminder time. Return JSON only.
Format:{"taskId":"...","suggestedReminderTime":<epoch_ms>,"adaptiveReason":"reason max 8 words"}
Task:{"i":"${summary.i}","t":"${summary.t}","due":${summary.due}h}
Pattern:usually completes ${avgHoursBefore.toInt()}h before deadline
Now:${com.studyhub.core.util.currentTimeMillis()}"""
    }

    // ── Response Parsers ──

    private fun parsePriorityResponse(
        raw: String,
        tasks: List<Task>
    ): List<PriorityResult> = try {
        println("Groq Raw Response: $raw")
        val clean = raw.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        
        val results = Json { ignoreUnknownKeys = true }
            .decodeFromString<List<PriorityResult>>(clean)
        
        println("Groq Decoded Result Count: ${results.size}")
        
        results.map { result ->
            // Match by finding the task whose ID contains the result.taskId string
            // This is safer since AI might still return shortened versions
            val matchingTask = tasks.find { it.id == result.taskId || it.id.contains(result.taskId) }
            if (matchingTask == null) {
                println("No match found for taskId: ${result.taskId}")
            }
            result.copy(taskId = matchingTask?.id ?: result.taskId)
        }
    } catch (e: Exception) {
        println("Groq Parse Error: ${e.message}")
        fallbackPrioritySort(tasks)
    }

    private fun parseReminderResponse(
        raw: String,
        task: Task
    ): ReminderSchedule = try {
        val clean = raw.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        val parsed = Json { ignoreUnknownKeys = true }
            .decodeFromString<ReminderSchedule>(clean)
        parsed.copy(taskId = task.id)
    } catch (e: Exception) {
        fallbackReminder(task)
    }

    // ── Fallback (tanpa AI) ──

    private fun fallbackPrioritySort(
        tasks: List<Task>
    ): List<PriorityResult> = tasks
        .sortedWith(
            compareByDescending<Task> {
                when (it.priority) {
                    Priority.HIGH -> 3
                    Priority.MEDIUM -> 2
                    Priority.LOW -> 1
                }
            }.thenBy { it.dueDate }
        )
        .mapIndexed { index, task ->
            PriorityResult(
                taskId = task.id,
                priorityOrder = index + 1,
                reason = "Diurutkan by priority + deadline"
            )
        }

    private fun fallbackReminder(task: Task) = ReminderSchedule(
        taskId = task.id,
        suggestedReminderTime = task.dueDate - 86_400_000L, // H-1
        adaptiveReason = "Default H-1 reminder"
    )
}

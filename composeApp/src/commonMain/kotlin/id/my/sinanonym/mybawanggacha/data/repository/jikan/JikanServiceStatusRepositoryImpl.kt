package id.my.sinanonym.mybawanggacha.data.repository.jikan

import id.my.sinanonym.mybawanggacha.data.remote.jikan.api.JikanRateLimiter
import id.my.sinanonym.mybawanggacha.domain.settings.model.JikanServiceStatus
import id.my.sinanonym.mybawanggacha.domain.settings.model.JikanServiceStatusState
import id.my.sinanonym.mybawanggacha.domain.settings.repository.JikanServiceStatusRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class JikanServiceStatusRepositoryImpl(
    private val client: HttpClient
) : JikanServiceStatusRepository {

    override val status: Flow<JikanServiceStatus> = flow {
        emit(JikanServiceStatus.Checking)

        while (true) {
            emit(probeStatus())
            delay(CHECK_INTERVAL_MS)
        }
    }

    private suspend fun probeStatus(): JikanServiceStatus {
        return runCatching {
            JikanRateLimiter.awaitTurn()

            val response = client.get("${BASE_URL}genres/anime")
            val statusCode = response.status.value

            if (statusCode in 200..299) {
                return@runCatching JikanServiceStatus.Active.copy(statusCode = statusCode)
            }

            parseStatus(
                statusCode = statusCode,
                responseText = runCatching { response.bodyAsText() }.getOrDefault("")
            )
        }.getOrElse { error ->
            JikanServiceStatus(
                state = JikanServiceStatusState.Down,
                type = "NetworkError",
                message = error.message ?: "Unable to reach Jikan service."
            )
        }
    }

    private fun parseStatus(
        statusCode: Int,
        responseText: String
    ): JikanServiceStatus {
        val errorObject = runCatching {
            json.parseToJsonElement(responseText).jsonObject
        }.getOrNull()

        val type = errorObject
            ?.get("type")
            ?.jsonPrimitive
            ?.contentOrNull
            .orEmpty()

        val message = errorObject
            ?.get("message")
            ?.jsonPrimitive
            ?.contentOrNull
            ?.ifBlank { null }
            ?: responseText.take(140).ifBlank { "Jikan request failed with HTTP $statusCode" }

        val isServiceAlive = statusCode in 400..499 ||
            type.equals("RateLimitException", ignoreCase = true)

        return JikanServiceStatus(
            state = if (isServiceAlive) {
                JikanServiceStatusState.Active
            } else {
                JikanServiceStatusState.Down
            },
            statusCode = statusCode,
            type = type,
            message = message
        )
    }

    private companion object {
        private const val BASE_URL = "https://api.jikan.moe/v4/"
        private const val CHECK_INTERVAL_MS = 120_000L

        private val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
}

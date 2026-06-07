package com.kosthub.app.testing

import com.kosthub.app.domain.model.Kost
import com.kosthub.app.domain.model.Profile
import com.kosthub.app.domain.repository.KostRepository
import com.kosthub.app.domain.repository.ProfileRepository
import com.kosthub.app.data.remote.api.GeminiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class FakeKostRepository : KostRepository {
    var shouldThrowError = false
    private val kostList = mutableListOf<Kost>()
    private val kostFlow = MutableStateFlow<List<Kost>>(emptyList())

    fun setKosts(list: List<Kost>) {
        kostList.clear()
        kostList.addAll(list)
        kostFlow.value = kostList.toList()
    }

    override suspend fun getAll(): List<Kost> {
        if (shouldThrowError) throw Exception("Test Database Error")
        return kostList.toList()
    }

    override fun getAllFlow(): Flow<List<Kost>> {
        return kostFlow
    }

    override suspend fun syncRemote() {
        if (shouldThrowError) throw Exception("Test Network Error")
    }

    override suspend fun getById(id: Long): Kost? {
        if (shouldThrowError) throw Exception("Test Database Error")
        return kostList.firstOrNull { it.id == id }
    }

    override suspend fun update(kost: Kost) {
        if (shouldThrowError) throw Exception("Test Database Error")
        val index = kostList.indexOfFirst { it.id == kost.id }
        if (index != -1) {
            kostList[index] = kost
            kostFlow.value = kostList.toList()
        }
    }
}

class FakeProfileRepository : ProfileRepository {
    var shouldThrowError = false
    var currentProfile: Profile? = Profile(1L, "Anonim", "anonim@kosthub.com", 0.0, 0.0)

    override suspend fun getProfile(): Profile? {
        if (shouldThrowError) throw Exception("Test Database Error")
        return currentProfile
    }

    override suspend fun saveProfile(profile: Profile) {
        if (shouldThrowError) throw Exception("Test Database Error")
        currentProfile = profile
    }

    override suspend fun clearProfile() {
        if (shouldThrowError) throw Exception("Test Database Error")
        currentProfile = Profile(1L, "Anonim", "anonim@kosthub.com", 0.0, 0.0)
    }
}

object MockGeminiServiceBuilder {
    fun build(responseJson: String, status: HttpStatusCode = HttpStatusCode.OK): GeminiService {
        val mockEngine = MockEngine { _ ->
            respond(
                content = responseJson,
                status = status,
                headers = headersOf("Content-Type", "application/json")
            )
        }
        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
        return GeminiService(httpClient)
    }

    fun buildSuccess(text: String): GeminiService {
        val json = """
            {
              "candidates": [
                {
                  "content": {
                    "parts": [
                      {
                        "text": "$text"
                      }
                    ]
                  }
                }
              ]
            }
        """.trimIndent()
        return build(json)
    }
}

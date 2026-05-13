package com.studyhub.data.remote

import com.studyhub.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GroqApiClient(private val httpClient: HttpClient) {

    suspend fun getChatCompletion(prompt: String): String {
        val response = httpClient.post(ApiConfig.groqBaseUrl + "/chat/completions") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${ApiConfig.groqApiKey}")
            setBody(
                GroqRequest(
                    model = ApiConfig.groqModel,
                    messages = listOf(
                        GroqMessage(role = "user", content = prompt)
                    )
                )
            )
        }
        return response.body<GroqResponse>().choices.first().message.content
    }
}

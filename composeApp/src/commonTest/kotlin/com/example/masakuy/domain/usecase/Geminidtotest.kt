package com.example.masakuy.domain.usecase

import com.example.masakuy.presentation.screens.api.GeminiDto
import com.example.masakuy.presentation.screens.api.GeminiService
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class GeminiDtoTest {

    @Test
    fun `PartRequest dan ContentRequest dan GeminiRequest`() {
        val part = GeminiDto.PartRequest(text = "halo")
        val content = GeminiDto.ContentRequest(parts = listOf(part))
        val request = GeminiDto.GeminiRequest(contents = listOf(content))

        assertEquals("halo", part.text)
        assertEquals(1, content.parts.size)
        assertEquals(1, request.contents.size)
        assertEquals(part, part.copy())
        assertEquals(content, content.copy())
        assertEquals(request, request.copy())
        assertTrue(part.toString().contains("halo"))
        assertTrue(content.toString().contains("ContentRequest"))
        assertTrue(request.toString().contains("GeminiRequest"))
        assertEquals(part.hashCode(), part.copy().hashCode())
        assertEquals("halo", part.component1())
    }

    @Test
    fun `Part Content Candidate dan GeminiResponse default null`() {
        val part = GeminiDto.Part()
        val content = GeminiDto.Content()
        val candidate = GeminiDto.Candidate()
        val response = GeminiDto.GeminiResponse()

        assertEquals(null, part.text)
        assertEquals(null, content.parts)
        assertEquals(null, candidate.content)
        assertEquals(null, response.candidates)
    }

    @Test
    fun `Part Content Candidate GeminiResponse dengan nilai terisi`() {
        val part = GeminiDto.Part(text = "isi")
        val content = GeminiDto.Content(parts = listOf(part))
        val candidate = GeminiDto.Candidate(content = content)
        val response = GeminiDto.GeminiResponse(candidates = listOf(candidate))

        assertEquals("isi", part.text)
        assertEquals(1, content.parts?.size)
        assertEquals(content, candidate.content)
        assertEquals(1, response.candidates?.size)

        assertEquals(part, part.copy())
        assertEquals(content, content.copy())
        assertEquals(candidate, candidate.copy())
        assertEquals(response, response.copy())

        assertTrue(part.toString().contains("isi"))
        assertTrue(content.toString().contains("Content"))
        assertTrue(candidate.toString().contains("Candidate"))
        assertTrue(response.toString().contains("GeminiResponse"))

        assertEquals(part.hashCode(), part.copy().hashCode())
        assertEquals(content.hashCode(), content.copy().hashCode())
        assertEquals(candidate.hashCode(), candidate.copy().hashCode())
        assertEquals(response.hashCode(), response.copy().hashCode())

        assertNotEquals(part, GeminiDto.Part(text = "lain"))
    }

    @Test
    fun `GroqRequest Message GroqResponse Choice`() {
        val message = GeminiService.Message(role = "user", content = "halo")
        val groqRequest = GeminiService.GroqRequest(model = "model-x", messages = listOf(message))
        val choice = GeminiService.Choice(message = message)
        val groqResponse = GeminiService.GroqResponse(choices = listOf(choice))

        assertEquals("user", message.role)
        assertEquals("halo", message.content)
        assertEquals(1000, groqRequest.max_tokens)
        assertEquals("model-x", groqRequest.model)
        assertEquals(message, choice.message)
        assertEquals(1, groqResponse.choices?.size)

        assertEquals(message, message.copy())
        assertEquals(groqRequest, groqRequest.copy())
        assertEquals(choice, choice.copy())
        assertEquals(groqResponse, groqResponse.copy())

        assertTrue(message.toString().contains("user"))
        assertTrue(groqRequest.toString().contains("GroqRequest"))
        assertTrue(choice.toString().contains("Choice"))
        assertTrue(groqResponse.toString().contains("GroqResponse"))

        assertEquals(message.hashCode(), message.copy().hashCode())
        assertEquals("user", message.component1())
        assertEquals("halo", message.component2())

        val groqRequestCustom = GeminiService.GroqRequest(model = "x", messages = emptyList(), max_tokens = 500)
        assertEquals(500, groqRequestCustom.max_tokens)
    }

    @Test
    fun `RateLimitException menyimpan retryAfterSeconds dan message`() {
        val ex = GeminiService.RateLimitException(45L)

        assertEquals(45L, ex.retryAfterSeconds)
        assertTrue(ex.message!!.contains("45"))
        assertTrue(ex.message!!.contains("Terlalu banyak"))
    }

    @Test
    fun `ApiException menyimpan message dengan benar`() {
        val ex = GeminiService.ApiException("API key tidak valid.")

        assertEquals("API key tidak valid.", ex.message)
    }

    @Test
    fun `GeminiResponse choices null tidak error saat diakses`() {
        val response = GeminiDto.GeminiResponse(candidates = null)
        val firstText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

        assertEquals(null, firstText)
    }
}
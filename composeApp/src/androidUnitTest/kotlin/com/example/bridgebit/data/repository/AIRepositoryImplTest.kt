package com.example.bridgebit.data.repository

import com.example.bridgebit.data.remote.api.GeminiService
import com.example.bridgebit.domain.repository.WritingStyle
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AIRepositoryImplTest {

    private lateinit var geminiService: GeminiService
    private lateinit var repository: AIRepositoryImpl

    @Before
    fun setup() {
        geminiService = mockk(relaxed = true)
        repository = AIRepositoryImpl(geminiService)
    }

    @Test
    fun `Test 21 - summarize should return generated content`() = runTest {
        val expectedResponse = "Ini adalah ringkasan."
        coEvery { geminiService.generateContent(any(), any()) } returns Result.success(expectedResponse)

        val result = repository.summarize("Teks yang sangat panjang untuk diuji.")

        assertEquals(Result.success(expectedResponse), result)
        coVerify { geminiService.generateContent(match { it.contains("Rangkum teks berikut") }, any()) }
    }

    @Test
    fun `Test 22 - generateIdeas should parse and return a clean list of ideas`() = runTest {
        // AI membalas dengan format yang kotor (ada nomor, baris kosong)
        val rawAiResponse = "1. Ide pertama\n\n2. Ide kedua\n3. Ide ketiga"
        coEvery { geminiService.generateContent(any(), any()) } returns Result.success(rawAiResponse)

        val result = repository.generateIdeas("Teknologi")
        val ideaList = result.getOrNull()

        // Pastikan nomor dibersihkan dan baris kosong dibuang
        assertEquals(3, ideaList?.size)
        assertEquals("Ide pertama", ideaList?.get(0))
        assertEquals("Ide ketiga", ideaList?.get(2))
    }

    @Test
    fun `Test 23 - improveWriting should use the correct WritingStyle instructions`() = runTest {
        coEvery { geminiService.generateContent(any(), any()) } returns Result.success("Teks rapi")

        repository.improveWriting("teks", WritingStyle.FORMAL)
        coVerify { geminiService.generateContent(match { it.contains("Gunakan gaya formal") }, any()) }

        repository.improveWriting("teks", WritingStyle.CASUAL)
        coVerify { geminiService.generateContent(match { it.contains("Gunakan gaya santai") }, any()) }
    }

    @Test
    fun `Test 24 - translate should include target language in prompt`() = runTest {
        coEvery { geminiService.generateContent(any(), any()) } returns Result.success("Matahari")

        repository.translate("Sun", "Indonesia")

        coVerify { geminiService.generateContent(match { it.contains("Terjemahkan ke bahasa Indonesia") }, any()) }
    }

    @Test
    fun `Test 25 - chat should pass message directly without system prompt`() = runTest {
        coEvery { geminiService.generateContent(prompt = "Halo AI", systemPrompt = null) } returns Result.success("Hai!")

        val result = repository.chat("Halo AI")
        assertEquals("Hai!", result.getOrNull())
    }

    @Test
    fun `Test 26 - suggestTitle should remove quotes from result`() = runTest {
        // AI sering membalas dengan tanda kutip
        coEvery { geminiService.generateContent(any(), any()) } returns Result.success("\"Judul Keren\"")

        val result = repository.suggestTitle("Konten blog")

        assertEquals("Judul Keren", result.getOrNull())
    }
}
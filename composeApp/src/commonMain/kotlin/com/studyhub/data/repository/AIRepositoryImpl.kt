package com.studyhub.data.repository

import com.studyhub.data.remote.api.GroqService
import com.studyhub.data.remote.api.SystemPrompts
import com.studyhub.domain.repository.AIRepository
import com.studyhub.domain.repository.WritingStyle

class AIRepositoryImpl(
    private val groqService: GroqService
) : AIRepository {
    
    override suspend fun summarize(text: String): Result<String> {
        val prompt = """
            Rangkum teks berikut:
            
            $text
        """.trimIndent()
        
        return groqService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.SUMMARIZER
        )
    }
    
    override suspend fun generateIdeas(topic: String): Result<List<String>> {
        val prompt = """
            Berikan 5 ide kreatif untuk topik: $topic
        """.trimIndent()
        
        return groqService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.IDEA_GENERATOR
        ).map { response ->
            response.lines()
                .filter { it.isNotBlank() }
                .map { line ->
                    line.replace(Regex("^\\d+\\.\\s*"), "").trim()
                }
                .filter { it.isNotBlank() }
        }
    }
    
    override suspend fun improveWriting(text: String, style: WritingStyle): Result<String> {
        val styleInstruction = when (style) {
            WritingStyle.FORMAL -> "Gunakan gaya formal dan profesional."
            WritingStyle.CASUAL -> "Gunakan gaya santai dan friendly."
            WritingStyle.ACADEMIC -> "Gunakan gaya akademik dan ilmiah."
            WritingStyle.CREATIVE -> "Gunakan gaya kreatif dan menarik."
            WritingStyle.NEUTRAL -> "Gunakan gaya netral."
        }
        
        val prompt = """
            $styleInstruction
            
            Perbaiki tulisan berikut:
            
            $text
        """.trimIndent()
        
        return groqService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.WRITING_IMPROVER
        )
    }
    
    override suspend fun translate(text: String, targetLanguage: String): Result<String> {
        val prompt = """
            Terjemahkan ke bahasa $targetLanguage:
            
            $text
        """.trimIndent()
        
        return groqService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.TRANSLATOR
        )
    }
    
    override suspend fun chat(message: String): Result<String> {
        return groqService.generateContent(prompt = message)
    }
    
    override suspend fun suggestTitle(content: String): Result<String> {
        val prompt = """
            Berikan saran judul untuk konten berikut:
            
            $content
        """.trimIndent()
        
        return groqService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.TITLE_SUGGESTER
        ).map { it.trim().removeSurrounding("\"") }
    }
}

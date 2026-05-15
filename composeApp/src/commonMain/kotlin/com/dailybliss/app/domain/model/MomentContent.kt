package com.dailybliss.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed class ContentBlock {
    @Serializable
    data class Text(val text: String) : ContentBlock()
    
    @Serializable
    data class Image(val url: String, val caption: String? = null) : ContentBlock()
}

@Serializable
data class MomentContent(
    val blocks: List<ContentBlock> = listOf(ContentBlock.Text(""))
)

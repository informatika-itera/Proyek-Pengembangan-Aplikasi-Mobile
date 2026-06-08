package id.my.sinanonym.mybawanggacha.presentation.screens.ai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import id.my.sinanonym.mybawanggacha.domain.ai.repository.ChatMessage
import id.my.sinanonym.mybawanggacha.domain.ai.repository.MessageSender
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiApiModel

internal data class AiMediaReference(
    val type: String,
    val malId: Int?,
    val title: String,
    val score: String?,
    val imageUrl: String?
) {
    val normalizedType: String
        get() = when (type.lowercase()) {
            "anime" -> "anime"
            "ln", "light novel", "light_novel", "novel" -> "manga"
            else -> "manga"
        }

    val displayType: String
        get() = when (type.lowercase()) {
            "anime" -> "Anime"
            "ln", "light novel", "light_novel", "novel" -> "Light Novel"
            else -> "Manga"
        }
}


@Composable
internal fun ChatBubble(
    message: ChatMessage,
    noteId: Long?,
    onCopy: () -> Unit,
    onApply: () -> Unit,
    onMediaClick: (AiMediaReference) -> Unit = {}
) {
    val isUser = message.sender == MessageSender.USER
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (isUser) {
            Card(
                shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.padding(start = 48.dp)
            ) {
                MarkdownText(
                    text = message.text,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    onMediaClick = onMediaClick
                )
            }
        } else {
            Card(
                shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.padding(end = 48.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    MarkdownText(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        onMediaClick = onMediaClick
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCopy,
                            modifier = Modifier.height(32.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Salin", style = MaterialTheme.typography.labelSmall)
                        }
                        if (noteId != null) {
                            Button(
                                onClick = onApply,
                                modifier = Modifier.height(32.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Terapkan", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun LoadingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.padding(end = 48.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI sedang mengetik...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
internal fun AiModelMenu(
    selected: AiApiModel,
    onSelected: (AiApiModel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(
                text = selected.label,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AiApiModel.entries.forEach { model ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = model.label,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = model.modelId,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        onSelected(model)
                    }
                )
            }
        }
    }
}

@Composable
private fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onMediaClick: (AiMediaReference) -> Unit = {}
) {
    val blocks = remember(text) { parseMarkdownBlocks(text) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        blocks.forEach { block ->
            when (block) {
                MarkdownBlock.Space -> Spacer(modifier = Modifier.height(2.dp))
                is MarkdownBlock.Code -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
                    ) {
                        Text(
                            text = block.value,
                            modifier = Modifier.padding(10.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = color
                        )
                    }
                }
                is MarkdownBlock.Line -> MarkdownLine(
                    value = block.value,
                    style = style,
                    color = color
                )
                is MarkdownBlock.Media -> MediaReferenceCard(
                    reference = block.reference,
                    onClick = onMediaClick
                )
            }
        }
    }
}

@Composable
private fun MediaReferenceCard(
    reference: AiMediaReference,
    onClick: (AiMediaReference) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = reference.malId != null) { onClick(reference) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.74f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = reference.imageUrl,
                contentDescription = reference.title,
                modifier = Modifier
                    .width(58.dp)
                    .height(82.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = reference.displayType,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = reference.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                reference.score?.takeIf { it.isNotBlank() }?.let { score ->
                    Text(
                        text = "Score $score",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = if (reference.malId != null) "Tap untuk buka detail" else "Detail belum bisa dibuka",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MarkdownLine(
    value: String,
    style: TextStyle,
    color: Color
) {
    val trimmed = value.trim()
    val heading = when {
        trimmed.startsWith("### ") -> trimmed.removePrefix("### ") to MaterialTheme.typography.titleSmall
        trimmed.startsWith("## ") -> trimmed.removePrefix("## ") to MaterialTheme.typography.titleMedium
        trimmed.startsWith("# ") -> trimmed.removePrefix("# ") to MaterialTheme.typography.titleLarge
        else -> null
    }

    if (heading != null) {
        Text(
            text = inlineMarkdown(heading.first),
            style = heading.second,
            color = color,
            fontWeight = FontWeight.Bold
        )
        return
    }

    val bulletPrefix = when {
        trimmed.startsWith("- ") -> "•" to trimmed.removePrefix("- ")
        trimmed.startsWith("* ") -> "•" to trimmed.removePrefix("* ")
        else -> null
    }

    if (bulletPrefix != null) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = bulletPrefix.first,
                style = style,
                color = color
            )
            Text(
                text = inlineMarkdown(bulletPrefix.second),
                modifier = Modifier.weight(1f),
                style = style,
                color = color
            )
        }
        return
    }

    val numbered = Regex("""^(\d+)\.\s+(.+)$""").find(trimmed)
    if (numbered != null) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "${numbered.groupValues[1]}.",
                style = style,
                color = color
            )
            Text(
                text = inlineMarkdown(numbered.groupValues[2]),
                modifier = Modifier.weight(1f),
                style = style,
                color = color
            )
        }
        return
    }

    val quote = trimmed.removePrefix("> ").takeIf { trimmed.startsWith("> ") }
    if (quote != null) {
        Text(
            text = inlineMarkdown(quote),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp),
            style = style,
            color = color,
            fontStyle = FontStyle.Italic
        )
        return
    }

    Text(
        text = inlineMarkdown(value),
        style = style,
        color = color
    )
}

private sealed interface MarkdownBlock {
    data class Line(val value: String) : MarkdownBlock
    data class Code(val value: String) : MarkdownBlock
    data class Media(val reference: AiMediaReference) : MarkdownBlock
    data object Space : MarkdownBlock
}

private fun parseMarkdownBlocks(text: String): List<MarkdownBlock> {
    val blocks = mutableListOf<MarkdownBlock>()
    val codeLines = mutableListOf<String>()
    val mediaLines = mutableListOf<String>()
    var inCode = false
    var inMedia = false

    text.lines().forEach { line ->
        val trimmed = line.trim()

        when {
            trimmed == ":::media" && !inCode -> {
                inMedia = true
                mediaLines.clear()
                return@forEach
            }
            trimmed == ":::" && inMedia -> {
                parseMediaReference(mediaLines)?.let { reference ->
                    blocks += MarkdownBlock.Media(reference)
                }
                mediaLines.clear()
                inMedia = false
                return@forEach
            }
            inMedia -> {
                mediaLines += line
                return@forEach
            }
            trimmed.startsWith("```") -> {
                if (inCode) {
                    blocks += MarkdownBlock.Code(codeLines.joinToString("\n").trimEnd())
                    codeLines.clear()
                    inCode = false
                } else {
                    inCode = true
                }
                return@forEach
            }
        }

        if (inCode) {
            codeLines += line
        } else if (line.isBlank()) {
            blocks += MarkdownBlock.Space
        } else {
            blocks += MarkdownBlock.Line(line)
        }
    }

    if (codeLines.isNotEmpty()) {
        blocks += MarkdownBlock.Code(codeLines.joinToString("\n").trimEnd())
    }
    parseMediaReference(mediaLines)?.let { reference ->
        blocks += MarkdownBlock.Media(reference)
    }

    return blocks.ifEmpty { listOf(MarkdownBlock.Line(text)) }
}

private fun parseMediaReference(lines: List<String>): AiMediaReference? {
    if (lines.isEmpty()) return null

    val values = lines
        .mapNotNull { line ->
            val index = line.indexOf("=")
            if (index <= 0) return@mapNotNull null
            line.take(index).trim().lowercase() to line.drop(index + 1).trim()
        }
        .toMap()

    val title = values["title"]?.takeIf { it.isNotBlank() } ?: return null

    return AiMediaReference(
        type = values["type"]?.takeIf { it.isNotBlank() } ?: "manga",
        malId = values["mal_id"]?.toIntOrNull(),
        title = title,
        score = values["score"],
        imageUrl = values["image_url"]
    )
}

private fun inlineMarkdown(value: String) = buildAnnotatedString {
    var index = 0

    while (index < value.length) {
        when {
            value.startsWith("**", index) -> {
                val end = value.indexOf("**", startIndex = index + 2)
                if (end > index) {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(value.substring(index + 2, end))
                    pop()
                    index = end + 2
                } else {
                    append(value[index])
                    index++
                }
            }
            value.startsWith("*", index) -> {
                val end = value.indexOf("*", startIndex = index + 1)
                if (end > index) {
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    append(value.substring(index + 1, end))
                    pop()
                    index = end + 1
                } else {
                    append(value[index])
                    index++
                }
            }
            value.startsWith("`", index) -> {
                val end = value.indexOf("`", startIndex = index + 1)
                if (end > index) {
                    pushStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    append(value.substring(index + 1, end))
                    pop()
                    index = end + 1
                } else {
                    append(value[index])
                    index++
                }
            }
            else -> {
                append(value[index])
                index++
            }
        }
    }
}

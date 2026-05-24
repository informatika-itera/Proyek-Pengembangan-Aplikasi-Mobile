package com.example.noteai.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val targetUrl: String = "",
    val vulnType: VulnType = VulnType.OTHER,
    val color: NoteColor = NoteColor.DEFAULT,
    val severity: VulnSeverity = VulnSeverity.NONE,
    val status: VulnStatus = VulnStatus.NEW,
    val isPinned: Boolean = false,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
) {
    val preview: String
        get() = if (content.length > 100) content.take(100) + "..." else content

    val isEmpty: Boolean
        get() = title.isBlank() && content.isBlank()
}

// Jenis kerentanan (vulnerability type)
enum class VulnType(val displayName: String) {
    XSS("Cross-Site Scripting (XSS)"),
    SQLI("SQL Injection (SQLi)"),
    IDOR("Insecure Direct Object Ref (IDOR)"),
    CSRF("Cross-Site Request Forgery (CSRF)"),
    RCE("Remote Code Execution (RCE)"),
    SSRF("Server-Side Request Forgery (SSRF)"),
    BAC("Broken Access Control"),
    INFO_DISC("Information Disclosure"),
    OPEN_REDIRECT("Open Redirect"),
    OTHER("Lainnya");

    companion object {
        fun fromString(value: String): VulnType {
            return entries.find { it.name == value } ?: OTHER
        }
    }
}

// Status tracking temuan bug bounty
enum class VulnStatus(val displayName: String, val colorHex: Long) {
    NEW("New", 0xFF8B949E),
    REPORTED("Reported", 0xFF58A6FF),
    TRIAGED("Triaged", 0xFFFFD700),
    ACCEPTED("Accepted", 0xFF00FF41),
    RESOLVED("Resolved", 0xFF00C832),
    PAID("Paid", 0xFF9333EA);

    companion object {
        fun fromString(value: String): VulnStatus {
            return entries.find { it.name == value } ?: NEW
        }
    }
}

// Severity level untuk vulnerability tracking
enum class VulnSeverity(val displayName: String, val colorHex: Long) {
    NONE("None", 0xFF8B949E),
    LOW("Low", 0xFF58A6FF),
    MEDIUM("Medium", 0xFFFFD700),
    HIGH("High", 0xFFFF8C00),
    CRITICAL("Critical", 0xFFFF4444);

    companion object {
        fun fromString(value: String): VulnSeverity {
            return entries.find { it.name == value } ?: NONE
        }
    }
}

enum class NoteColor(val hexValue: Long) {
    DEFAULT(0xFFFFFFFF),
    RED(0xFFFFCDD2),
    ORANGE(0xFFFFE0B2),
    YELLOW(0xFFFFF9C4),
    GREEN(0xFFC8E6C9),
    BLUE(0xFFBBDEFB),
    PURPLE(0xFFE1BEE7),
    PINK(0xFFF8BBD9);

    companion object {
        fun fromString(value: String): NoteColor {
            return entries.find { it.name == value } ?: DEFAULT
        }
    }
}
package com.dailybliss.app.core.util

/**
 * PlatformContext - expect declaration
 * 
 * Digunakan untuk menyediakan abstraksi Context platform-specific
 * (seperti android.content.Context di Android) agar bisa di-pass
 * ke dalam constructor expect class secara type-safe.
 */
expect abstract class PlatformContext

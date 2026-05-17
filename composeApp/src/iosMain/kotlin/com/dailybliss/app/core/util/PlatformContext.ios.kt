package com.dailybliss.app.core.util

/**
 * iOS implementation of PlatformContext
 * 
 * Di iOS, kita tidak membutuhkan Context object seperti di Android,
 * jadi kita gunakan empty class sebagai placeholder.
 * Kita menggunakan abstract class untuk menyesuaikan dengan expect declaration,
 * dan menyediakan concrete implementation di bawahnya.
 */
actual abstract class PlatformContext

class IosPlatformContext : PlatformContext()

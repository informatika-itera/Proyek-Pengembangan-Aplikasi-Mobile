# ── MasakuY ProGuard Rules ────────────────────────────────────────────────────

# Keep domain models (serialized ke/dari Gemini response)
-keep class com.example.masakuy.domain.model.** { *; }
-keep class com.example.masakuy.data.remote.dto.** { *; }

# SQLDelight — generated query classes
-keep class com.example.masakuy.data.local.** { *; }
-keep class app.cash.sqldelight.** { *; }
-dontwarn app.cash.sqldelight.**

# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**
-keep class kotlinx.coroutines.** { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.example.masakuy.**$$serializer { *; }
-keepclassmembers class com.example.masakuy.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.masakuy.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Google Generative AI SDK
-keep class com.google.ai.client.generativeai.** { *; }
-dontwarn com.google.ai.client.generativeai.**
-keep class com.google.protobuf.** { *; }
-dontwarn com.google.protobuf.**

# Koin
-keep class org.koin.** { *; }
-keep class com.example.masakuy.core.di.** { *; }
-dontwarn org.koin.**

# Kotlin
-keepclassmembernames class kotlinx.** { volatile <fields>; }
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Enum
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Coil
-dontwarn coil.**
# Ignore missing JDBC classes from sqlite-jdbc (not used on Android)
-dontwarn java.sql.**
-dontwarn org.sqlite.**

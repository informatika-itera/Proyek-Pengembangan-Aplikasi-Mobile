# ProGuard Rules for NoteAI
# ===========================

# Keep Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep serializable classes
-keep,includedescriptorclasses class com.example.noteai.**$$serializer { *; }
-keepclassmembers class com.example.noteai.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.noteai.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn io.netty.**
-dontwarn com.typesafe.**
-dontwarn org.slf4j.**

# Ktor common code mereferensikan JVM-only API (java.lang.management.*)
# lewat IntellijIdeaDebugDetector. Tidak ada di Android runtime → silence-kan.
-dontwarn io.ktor.util.debug.**
-dontwarn java.lang.management.**

# Keep SQLDelight generated classes
-keep class com.example.noteai.data.local.** { *; }

# Keep Koin DI metadata + ViewModel constructors agar reflection-based
# resolution tidak ke-strip oleh R8.
-keep class org.koin.** { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

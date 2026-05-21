# Add project-specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/Shared/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the configuration file and add direct link to your custom rules.

# Kotlin Serialization
-keep,includedescriptorclasses class com.example.mapenumkm.**$$serializer { *; }
-keepclassmembers class com.example.mapenumkm.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.mapenumkm.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Koin
-keep class org.koin.** { *; }

# SQLDelight
-keep class com.example.mapenumkm.data.local.** { *; }

# Compose Multiplatform
-keep class androidx.compose.** { *; }
-keep class org.jetbrains.compose.** { *; }

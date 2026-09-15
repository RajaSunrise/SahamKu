# Optimization rules
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Keep Data Models for Gson serialization
-keepclassmembers class com.investra.app.data.model.** { *; }
-keep class com.investra.app.data.model.** { *; }

# OkHttp & Gson rules
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class com.google.gson.** { *; }

# Jetpack Compose rules
-keep class androidx.compose.** { *; }

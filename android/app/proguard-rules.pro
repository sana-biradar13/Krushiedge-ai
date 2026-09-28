# ProGuard rules for KrushiEdge AI

# Keep Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.krushiedge.**$$serializer { *; }
-keepclassmembers class com.krushiedge.** {
    *** Companion;
}
-keepclasseswithmembers class com.krushiedge.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Retrofit interfaces
-keep,allowobfuscation interface com.krushiedge.ai.api.AiApiService
-keep,allowobfuscation interface com.krushiedge.data.remote.**

# Keep Room entities
-keep class com.krushiedge.data.local.entity.** { *; }

# Keep TensorFlow Lite
-keep class org.tensorflow.lite.** { *; }
-keepclassmembers class org.tensorflow.lite.** { *; }

# Keep AI model classes
-keep class com.krushiedge.ai.model.** { *; }

# Keep domain models
-keep class com.krushiedge.domain.model.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.** { *; }

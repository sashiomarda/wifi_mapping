# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Preserve line numbers and source file names for readable stack traces in crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preserve annotations and generic signatures
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep all classes/members annotated with @Keep
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# Keep database models and entities (Room)
-keep class com.sashiomarda.wifimapping.data.** { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep class * extends androidx.room.RoomDatabase

# Keep Retrofit & network interfaces/models
-keep class com.sashiomarda.wifimapping.network.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**

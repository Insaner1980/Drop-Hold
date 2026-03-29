# LibGDX
-keep class com.badlogic.gdx.** { *; }
-keep class com.badlogic.gdx.physics.bullet.** { *; }
-keepclassmembers class com.badlogic.gdx.physics.bullet.** {
    native <methods>;
}

# gdx-gltf
-keep class net.mgsx.gltf.** { *; }

# Fleks
-keep class io.github.quillraven.fleks.** { *; }

# libgdx-oboe
-keep class barsoosayque.libgdxoboe.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase

# Google Play Billing
-keep class com.android.vending.billing.** { *; }

# AdMob
-keep class com.google.android.gms.ads.** { *; }

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-keep,includedescriptorclasses class com.finntek.dropandhold.**$$serializer { *; }
-keepclassmembers class com.finntek.dropandhold.** {
    *** Companion;
}
-keepclasseswithmembers class com.finntek.dropandhold.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Protobuf Lite
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }

# Keep game model classes (Parcelable)
-keep class com.finntek.dropandhold.game.model.** { *; }

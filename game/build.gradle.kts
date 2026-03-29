plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
}

// Must match gdx version in libs.versions.toml
val gdxVersion = "1.14.0"

android {
    namespace = "com.finntek.dropandhold.game"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs("../assets")
        }
    }
}

dependencies {
    // LibGDX core
    api(libs.gdx)
    implementation(libs.gdx.backend.android)

    // LibGDX platform natives
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")

    // Bullet physics
    api(libs.gdx.bullet)
    implementation("com.badlogicgames.gdx:gdx-bullet-platform:$gdxVersion:natives-arm64-v8a")
    implementation("com.badlogicgames.gdx:gdx-bullet-platform:$gdxVersion:natives-armeabi-v7a")
    implementation("com.badlogicgames.gdx:gdx-bullet-platform:$gdxVersion:natives-x86_64")

    // Freetype
    implementation(libs.gdx.freetype)
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-arm64-v8a")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-armeabi-v7a")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-x86_64")

    // gdx-gltf (PBR rendering)
    implementation(libs.gdx.gltf)

    // KTX extensions
    implementation(libs.ktx.app)
    implementation(libs.ktx.assets)
    implementation(libs.ktx.graphics)
    implementation(libs.ktx.math)
    implementation(libs.ktx.scene2d)
    implementation(libs.ktx.freetype)

    // Fleks ECS
    implementation(libs.fleks)

    // TODO: libgdx-oboe low-latency audio — add when implementing audio system

    // AndroidX (for sensor, vibrator access)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)

    // Kotlin coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Testing
    testImplementation(libs.junit5)
    testImplementation(libs.mockk)
}

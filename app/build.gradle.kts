import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.serialization)

    id("com.google.dagger.hilt.android")
}

kotlin {
    target {
        compilerOptions{
            jvmTarget = JvmTarget.fromTarget("11")
        }
    }
}

android {
    namespace = "com.example.campussync"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.campussync"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // serialization
    implementation(libs.kotlinx.serialization.json)
    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android) // Android specific.
    implementation(libs.koin.androidx.compose) // Android + Compose specific.
    implementation(libs.koin.androidx.compose.navigation) // Android + Compose specific.
    implementation(libs.koin.ktor) // Ktor specific.
    implementation(libs.koin.test) // Test specific.
    // Ktor
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.auth)

    implementation(libs.hilt.android)
    implementation(libs.androidx.compose.material3)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.retrofit2.retrofit)

    // Stomp + messaging
    implementation("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")
    // Reactive Streams (Required by Stomp)
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    // JSON Serialization
    implementation("com.google.code.gson:gson:2.10.1")

    // ZXing core for QR code generation
    implementation(libs.zxing.core)

    // CameraX dependencies
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation(libs.camerax.extensions)

    // ML Kit Barcode Scanning
    implementation(libs.barcode.scanning)
    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)


    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.java.jwt)

    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.androidx.preference.ktx)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.material.icons.extended)

    implementation("br.com.devsrsouza.compose.icons:font-awesome:1.1.1")
    implementation("br.com.devsrsouza.compose.icons:octicons:1.1.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
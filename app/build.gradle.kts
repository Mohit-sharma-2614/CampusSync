plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")

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
        
        getByName("debug"){
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    flavorDimensions += "version"
    productFlavors {
        create("demo"){
            dimension = "version"
            applicationIdSuffix = ".demo"
            versionNameSuffix = "-demo"
        }
        create("full"){
            dimension = "version"
            applicationIdSuffix = ".full"
            versionNameSuffix = "-full"
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.retrofit2.retrofit)
//    implementation(libs.retrofit2.kotlinx.serialization.converter)
//    implementation(libs.converter.kotlinx.serialization)
//    implementation(libs.kotlinx.serialization.json)

    // ZXing core for QR code generation
    implementation(libs.core)

    // CameraX dependencies
    val cameraxVersion = "1.3.4" // Check for the latest stable version
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions) // Optional, but good for camera features

    // ML Kit Barcode Scanning
    implementation(libs.barcode.scanning) // Check for the latest version
    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core) // Check for the latest version
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

    implementation(libs.font.awesome)
    implementation(libs.octicons)

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
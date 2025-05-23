plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.1.21-2.0.1"
}

android {
    namespace = "com.example.campussync"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.campussync"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // Google Fonts
//    implementation("androidx.compose.ui:ui-text-google-fonts:3.0.0")


    // Extended Icons
    implementation("androidx.compose.material:material-icons-extended")

    /* ---------- Lifecycle & ViewModel ---------- */
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")         // lifecycleScope, etc. :contentReference[oaicite:3]{index=3}
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0")       // ViewModel + coroutines :contentReference[oaicite:4]{index=4}
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")   // ViewModel in Compose :contentReference[oaicite:5]{index=5}

    /* ---------- Navigation (Compose) ---------- */
    implementation("androidx.navigation:navigation-compose:2.9.0")           // composable NavHost :contentReference[oaicite:6]{index=6}

    /* ---------- Local DB – Room (runtime + KSP code-gen) ---------- */
    val roomVersion = "2.7.1"                                                // latest stable :contentReference[oaicite:7]{index=7}
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")                    // suspend fun DAO helpers
    ksp("androidx.room:room-compiler:$roomVersion")                          // annotation processor (KSP)

    /* ---------- Shared storage – DataStore ---------- */
    implementation("androidx.datastore:datastore-preferences:1.1.5")         // stable stream-based prefs :contentReference[oaicite:8]{index=8}

    /* ---------- Images ---------- */
    implementation("io.coil-kt:coil-compose:2.7.0")                          // Compose + Coroutines image loader :contentReference[oaicite:9]{index=9}

    /* ---------- Optional but handy ---------- */
    // Hilt DI (works with KSP)
    val hiltVersion = "2.54"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-compiler:$hiltVersion")

    // Tests & tooling
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.04.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")


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
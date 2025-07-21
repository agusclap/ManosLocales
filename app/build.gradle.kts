plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")  // Firebase
    id("kotlin-kapt")                     // Si usás Room, Hilt, etc.
}

android {
    namespace = "com.undef.manoslocales"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.undef.manoslocales"
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
    // ✅ Firebase BoM (una sola vez y al principio)
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    // ✅ Google Play Services (ubicación)
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // ✅ Accompanist y lifecycle
    implementation("com.google.accompanist:accompanist-permissions:0.28.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // ✅ Compose y UI
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("com.google.android.material:material:1.8.0")
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material3:material3:1.2.0")

    // ✅ Imágenes
    implementation("io.coil-kt:coil-compose:2.4.0")

    // ✅ DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // ✅ Cloudinary (excluye conflicto con Litr)
    implementation("com.cloudinary:cloudinary-android:1.30.0") {
        exclude(group = "com.linkedin.android.litr", module = "litr")
    }

    // ✅ Seguridadd
    implementation("org.mindrot:jbcrypt:0.4")

    // ✅ Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

plugins {
    alias(libs.plugins.android.application)
    // Plugin necesario para Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.codechallenge"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.codechallenge"
        minSdk = 27
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
}

dependencies {
    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth:22.3.0")
    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    // Firebase Firestore
    implementation("com.google.firebase:firebase-firestore:24.11.0")
    // Picasso para cargar imágenes
    implementation("com.squareup.picasso:picasso:2.8")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.vsl"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vsl"
        minSdk = 26 // Obligatoire pour les icones adaptatives & POI/log4j
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas"
                )
            }
        }
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10" // adapté pour Compose 1.6.x
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.material) // <= Ajouté
    implementation(libs.material.icons.extended) // <= Ajouté
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    implementation(libs.poi)
    implementation(libs.poi.ooxml)
    implementation(libs.xmlbeans)
    implementation(libs.navigation.compose)
    implementation(libs.coroutines.android)
    implementation(libs.lifecycle.viewmodel.ktx)
    // Optionnel: tooling preview/debug
    debugImplementation(libs.ui.tooling)
    implementation(libs.datastore.preferences)
}
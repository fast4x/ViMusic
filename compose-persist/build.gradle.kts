
plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.kotlin.compose)
}


/*
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    //alias(libs.plugins.kotlin.compose)
}
 */

android {
    namespace = "it.fast4x.compose.persist"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        //targetSdk = 34
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    sourceSets.all {
        kotlin.srcDir("src/$name/kotlin")
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

//    composeOptions {
//        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
//    }

    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    implementation(libs.compose.foundation)
}

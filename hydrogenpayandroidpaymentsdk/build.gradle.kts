plugins {
    id("com.android.library")
    id("maven-publish")
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.hydrogen.hydrogenpayandroidpaymentsdk"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        targetSdk = 34
        // versionCode = 1
        //versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }
    dataBinding {
        enable = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Dependencies
    implementation(libs.otp.view)
    implementation(libs.coroutines.dependency)
    implementation(libs.retrofit.dependency)
    implementation(libs.gson.converter)
    implementation(libs.nav.fragment)
    implementation(libs.nav.ui)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.logging.interceptor.dependency)
    implementation(libs.dprefs.library)
    // Room
    implementation(libs.androidx.room.ktx)
    kapt(libs.room.annotation.processor.dependency)
    implementation(libs.room.dependency)
    implementation(libs.life.cycle)
    // Balloon
    implementation(libs.balloon.alert.dependency)
    // Shimmer Effect
    implementation(libs.shimmer.effect.dependency)

    implementation(libs.coil.dependency)
    implementation(libs.coil.svg.dependency)
    implementation(libs.view.model.kts)
    implementation(libs.view.model.save.state.instance)
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.fragment.nav)
    implementation(libs.ui.nav)
}
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.hydrogen.hydrogenpaymentsdk"
                artifactId = "hydrogenpaymentsdk"
                version = "1.0.0"
            }
        }
    }
}
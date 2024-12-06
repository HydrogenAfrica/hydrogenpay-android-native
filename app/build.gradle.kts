plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.hydrogen.hydrogenpaymentsdk"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hydrogen.hydrogenpaymentsdk"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

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

    implementation(libs.coil.dependency)
    implementation(libs.view.model.kts)
    implementation(libs.view.model.save.state.instance)
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.fragment.nav)
    implementation(libs.ui.nav)
}
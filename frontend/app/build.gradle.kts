plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    id("kotlin-kapt")
}

android {
    namespace = "com.example.road_app_dip"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.road_app_dip"
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
}

dependencies {


    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.okhttp)
    implementation(libs.converter.gson)
    implementation(libs.retrofit)

    implementation (libs.play.services.maps.v1820)
    implementation (libs.androidx.fragment.ktx)


    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.awareness)
    kapt(libs.compiler)

    implementation(libs.play.services.maps.v1701)
    implementation(libs.play.services.location)

        implementation(libs.glide.v4160)
        annotationProcessor(libs.compiler.v4120)



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}


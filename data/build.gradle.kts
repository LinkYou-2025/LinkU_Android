plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Hilt
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.material)
    implementation(libs.firebase.messaging.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    // 의존성 정의
    implementation(project(":core"))
    implementation(project(":design"))

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.complier)
    implementation(libs.androidx.hilt.navigation)

//    // Retrofit2
//    implementation(libs.retrofit2)
//    implementation(libs.retrofit2.converter.gson)
//    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
//    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
//    implementation("com.squareup.moshi:moshi-kotlin:1.15.2")
//
//    // SharedPreference
//    implementation(libs.preference.ktx)
}
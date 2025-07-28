plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.core"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        //testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
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

    // design 모듈 의존성
    implementation(project(":design"))

    // ✅ Retrofit2 & OkHttp 추가 (버전은 app 모듈과 동일한 libs 사용)
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter.gson)
    implementation("com.squareup.okhttp3:okhttp:4.11.0") // OkHttp
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // 로깅

    // ✅ Gson (Retrofit gson converter에서 필요)
    implementation("com.google.code.gson:gson:2.10.1")

    // ✅ 코틀린 런타임 (이미 있음)
    implementation(libs.androidx.runtime.android)
}
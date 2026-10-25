import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    //alias(libs.plugins.kotlin.android)

    // Hilt
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.linku.core"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        //testInstrumentationRunner = libs.versions.testInstrumentationRunner.get()
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
    buildFeatures {
        buildConfig = true
    }
    testOptions {
        // 단위 테스트에서 android.util.Log 같은 프레임워크 호출이 예외 대신 기본값을 돌려주게 함.
        unitTests.isReturnDefaultValues = true
    }
/*    kotlinOptions {
        jvmTarget = "11"
    }*/
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {

    api(project(":core:navigation"))


    // ✅ Retrofit2 & OkHttp 추가 (버전은 app 모듈과 동일한 libs 사용)
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.okhttp) // OkHttp

    // ✅ Gson (Retrofit gson converter에서 필요)
    implementation(libs.gson)

//    implementation(project(":data"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

//    implementation(libs.gson)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation)

    implementation(libs.androidx.datastore.preferences)

    //Paging3
    implementation(libs.paging.runtime)

}

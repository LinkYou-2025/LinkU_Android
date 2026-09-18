import com.android.build.api.variant.BuildConfigField
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    //alias(libs.plugins.kotlin.android)

    // Hilt
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

@Suppress("UNCHECKED_CAST")
val linkuConfigProviders =
    rootProject.extra["linkuConfigProviders"] as Map<String, Provider<String>>

@Suppress("UNCHECKED_CAST")
val linkuBuildConfigString =
    rootProject.extra["linkuBuildConfigString"] as
        (Provider<String>) -> Provider<BuildConfigField<String>>

// release(로컬/CI 공통)는 SERVER_DOMAIN/API_VERSION을,
// debug는 DEV_SERVER_DOMAIN/DEV_API_VERSION을 쓴다.
// 모두 local.properties(각자 로컬) 또는 환경 변수에서 읽어오며, 값 자체를 Gradle에 하드코딩하지 않는다.
val releaseServerBaseUrlProvider =
    linkuConfigProviders.getValue("SERVER_DOMAIN").flatMap { serverDomain ->
        linkuConfigProviders.getValue("API_VERSION").map { apiVersion ->
            "$serverDomain/$apiVersion/"
        }
    }

val debugServerBaseUrlProvider =
    linkuConfigProviders.getValue("DEV_SERVER_DOMAIN").flatMap { serverDomain ->
        linkuConfigProviders.getValue("DEV_API_VERSION").map { apiVersion ->
            "$serverDomain/$apiVersion/"
        }
    }

android {
    namespace = "com.linku.data"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = libs.versions.testInstrumentationRunner.get()
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
/*    kotlinOptions {
        jvmTarget = "11"
    }*/
    buildFeatures {
        buildConfig = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

androidComponents {
    onVariants { variant ->
        val baseUrlProvider = if (variant.buildType == "debug") {
            debugServerBaseUrlProvider
        } else {
            releaseServerBaseUrlProvider
        }
        variant.buildConfigFields?.put(
            "SERVER_BASE_URL",
            linkuBuildConfigString(baseUrlProvider)
        )
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.material)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    implementation(libs.gson)

    // 의존성 정의
    implementation(project(":core"))
    implementation(project(":design"))

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)

    // Retrofit2
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.converter.moshi)
    // release 빌드에서도 심볼 해석이 되어야 해서 implementation으로 포함하고,
    // 실제 로깅 여부는 ServerApiModule에서 BuildConfig.DEBUG로 런타임 분기함.
    implementation(libs.okhttp.logging.interceptor)

    // SharedPreference
    implementation(libs.preference.ktx)

    // datastore
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.kotlinx.serialization.json)

    // paging3
    implementation(libs.paging.runtime)

    //fcm
    implementation(libs.kotlinx.coroutines.play.services)
}

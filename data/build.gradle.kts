import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    //alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Hilt
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

val localProperties = rootProject.extra["localProperties"] as Properties

val serverDomain = localProperties.getProperty("SERVER_DOMAIN")
    ?.trim()
    ?.takeIf { it.isNotEmpty() }
    ?: throw GradleException(
        "SERVER_DOMAIN is missing or blank. Set it in local.properties, " +
            "or set the SERVER_DOMAIN environment variable."
    )

val apiVersion = localProperties.getProperty("API_VERSION")
    ?.trim()
    ?.takeIf { it.isNotEmpty() }
    ?: throw GradleException(
        "API_VERSION is missing or blank. Set it in local.properties, " +
            "or set the API_VERSION environment variable."
    )

val serverBaseUrl = "$serverDomain/$apiVersion/"

android {
    namespace = "com.linku.data"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = libs.versions.testInstrumentationRunner.get()
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "SERVER_BASE_URL", "\"$serverBaseUrl\"")
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
    implementation(libs.firebase.messaging.ktx)
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

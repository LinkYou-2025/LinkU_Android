import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    //alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")

    // Hilt
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}

val kakaoNativeAppKey = localProperties.getProperty("KAKAO_NATIVE_APP_KEY")
    ?.trim()
    ?.takeIf { it.isNotEmpty() }
    ?: throw GradleException("KAKAO_NATIVE_APP_KEY is missing or blank in local.properties")

// 구글 소셜 로그인 로컬 프로퍼티
val googleWebClientId = localProperties.getProperty("GOOGLE_WEB_CLIENT_ID")
    ?.trim()
    ?.takeIf { it.isNotEmpty() }
    ?: throw GradleException("GOOGLE_WEB_CLIENT_ID is missing or blank in local.properties")

val serverDomain = localProperties.getProperty("SERVER_DOMAIN")
    ?.trim()
    ?.takeIf { it.isNotEmpty() }
    ?: throw GradleException("SERVER_DOMAIN is missing or blank in local.properties")

val serverHost = serverDomain
    .removePrefix("https://")
    .removePrefix("http://")
    .trimEnd('/')

android {
    namespace = "com.linku"

    compileSdk = libs.versions.compileSdk.get().toInt()


    defaultConfig {
        applicationId = "com.linku"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.appVersionCode.get().toInt()
        versionName = libs.versions.appVersionName.get()
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = libs.versions.testInstrumentationRunner.get()
        buildConfigField(
            "String",
            "KAKAO_NATIVE_APP_KEY",
            "\"$kakaoNativeAppKey\""
        )
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")
        buildConfigField("String", "SERVER_DOMAIN", "\"$serverDomain\"")
        // 로컬 프로퍼티에 각자 디버그 키(개발 테스트) 꼭 넣어서 주세요. 안 그러면 실행 안됩니다.
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoNativeAppKey
        manifestPlaceholders["SERVER_HOST"] = serverHost
    }
    buildFeatures {
        buildConfig = true
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
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.androidx.compose.material.icons.extended)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.gson)

    // 의존성 정의
    implementation(project(":feature:login"))
    implementation(project(":feature:home"))
    implementation(project(":feature:file"))
    implementation(project(":feature:curation"))
    implementation(project(":feature:mypage"))
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":design"))

    // Retrofit2
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.converter.moshi)
    implementation(libs.logging.interceptor)
    implementation(libs.moshi.kotlin)

    // SharedPreference
    implementation(libs.preference.ktx)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    ksp(libs.androidx.hilt.compiler)

    implementation(libs.androidx.hilt.navigation)
    implementation(libs.androidx.hilt.navigation.compose)

    // coil
    implementation(libs.coil.compose)

    // FCM
    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation(libs.firebase.analytics)

    // 카카오 로그인
    implementation(libs.v2.user)

    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.kotlinx.coroutines.android)

    // Paging
    implementation(libs.paging.runtime)

    implementation(libs.lottie)

}

import com.android.build.api.variant.BuildConfigField
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    //alias(libs.plugins.kotlin.android)

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")

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

@Suppress("UNCHECKED_CAST")
val linkuManifestValue =
    rootProject.extra["linkuManifestValue"] as (Provider<String>) -> Provider<String>

val releaseKakaoNativeAppKeyProvider = linkuConfigProviders.getValue("KAKAO_NATIVE_APP_KEY")
val debugKakaoNativeAppKeyProvider = linkuConfigProviders.getValue("DEV_KAKAO_NATIVE_APP_KEY")
val releaseServerDomainProvider = linkuConfigProviders.getValue("SERVER_DOMAIN")
val debugServerDomainProvider = linkuConfigProviders.getValue("DEV_SERVER_DOMAIN")
val releaseServerHostProvider = linkuConfigProviders.getValue("SERVER_HOST")
val debugServerHostProvider = linkuConfigProviders.getValue("DEV_SERVER_HOST")

val keystoreProperties = Properties().apply {
    val file = rootProject.file("key.properties")
    if (file.exists()) load(file.inputStream())
}

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
    }

    signingConfigs {
        create("release") {
            storeFile = keystoreProperties.getProperty("storeFile")?.let { rootProject.file(it) }
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
            keystoreProperties.getProperty("storeType")?.let { storeType = it }
        }
    }

    buildTypes {
        debug {
            versionNameSuffix = "-debug"
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
}

androidComponents {
    onVariants { variant ->
        val kakaoNativeAppKeyProvider = if (variant.buildType == "debug") {
            debugKakaoNativeAppKeyProvider
        } else {
            releaseKakaoNativeAppKeyProvider
        }
        variant.buildConfigFields?.put(
            "KAKAO_NATIVE_APP_KEY",
            linkuBuildConfigString(kakaoNativeAppKeyProvider)
        )
        val serverDomainProvider = if (variant.buildType == "debug") {
            debugServerDomainProvider
        } else {
            releaseServerDomainProvider
        }
        val serverHostProvider = if (variant.buildType == "debug") {
            debugServerHostProvider
        } else {
            releaseServerHostProvider
        }
        variant.buildConfigFields?.put(
            "SERVER_DOMAIN",
            linkuBuildConfigString(serverDomainProvider)
        )
        variant.buildConfigFields?.put(
            "SERVER_HOST",
            linkuBuildConfigString(serverHostProvider)
        )
        variant.manifestPlaceholders.put(
            "KAKAO_NATIVE_APP_KEY",
            linkuManifestValue(kakaoNativeAppKeyProvider)
        )
        variant.manifestPlaceholders.put(
            "SERVER_HOST",
            linkuManifestValue(serverHostProvider)
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
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // 앱 조립: 화면과 내비게이션은 shell이, 공통 계약은 core가 가집니다.
    // 기능 모듈과 data, design은 shell을 통해 전이 의존으로 포함되며 Hilt가 그래프를 조립합니다.
    implementation(project(":shell"))
    implementation(project(":core"))
    implementation(project(":data"))

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // FCM
    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    // When using the BoM, don't specify versions in Firebase dependencies
    implementation(libs.firebase.analytics)

    // 카카오 로그인
    implementation(libs.v2.user)
}

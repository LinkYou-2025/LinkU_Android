import com.android.build.api.variant.BuildConfigField
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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

val kakaoNativeAppKeyProvider = linkuConfigProviders.getValue("KAKAO_NATIVE_APP_KEY")
val serverDomainProvider = linkuConfigProviders.getValue("SERVER_DOMAIN")
val serverHostProvider = linkuConfigProviders.getValue("SERVER_HOST")

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
        compose = true
        buildConfig = true
    }
}

androidComponents {
    onVariants { variant ->
        variant.buildConfigFields?.put(
            "KAKAO_NATIVE_APP_KEY",
            linkuBuildConfigString(kakaoNativeAppKeyProvider)
        )
        variant.buildConfigFields?.put(
            "SERVER_DOMAIN",
            linkuBuildConfigString(serverDomainProvider)
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
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
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
    implementation(libs.firebase.messaging)


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

    implementation(libs.lottie)

    // coil 의존성 추가 -> 추후 리팩토링 진행 시 feature 모듈로 따로 이동할 때 삭제할 예정
    implementation(libs.coil3.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.svg)
    implementation(libs.accompanist.systemuicontroller)

    // paging
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)

}

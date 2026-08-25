import java.util.Properties

// Top-level build file where you can add configuration options common to all subprojects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false

    // Hilt
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false

    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.5.0" apply false
}

val localPropertyEnvironmentNames = mapOf(
    "KAKAO_NATIVE_APP_KEY" to "LINKU_KAKAO_NATIVE_APP_KEY",
    "GOOGLE_WEB_CLIENT_ID" to "LINKU_GOOGLE_WEB_CLIENT_ID",
    "SERVER_DOMAIN" to "LINKU_SERVER_DOMAIN",
    "SERVER_HOST" to "LINKU_SERVER_HOST",
    "API_VERSION" to "LINKU_API_VERSION",
)

val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties().apply {
    if (localPropertiesFile.isFile) {
        localPropertiesFile.inputStream().use { load(it) }
    }

    localPropertyEnvironmentNames.forEach { (propertyName, environmentVariableName) ->
        val propertyValue = getProperty(propertyName)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: project.providers.environmentVariable(environmentVariableName).orNull
                ?.trim()
                ?.takeIf { it.isNotEmpty() }

        if (propertyValue == null) {
            remove(propertyName)
        } else {
            setProperty(propertyName, propertyValue)
        }
    }
}

rootProject.extra["localProperties"] = localProperties

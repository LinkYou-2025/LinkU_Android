import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import java.util.Properties

// Top-level build file where you can add configuration options common to all subprojects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false

    // Hilt
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false

    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.5.0" apply false
}

val linkuVersionCodePropertyName = "linkuVersionCode"
val linkuVersionCodeUsage = "-PlinkuVersionCode=<positive-base-10-integer>"
val maxGooglePlayVersionCode = 2_100_000_000L

/**
 * Validates the release-only versionCode contract without exposing the supplied value.
 */
fun parseLinkuVersionCode(rawValue: String): Int {
    val value = rawValue.trim()

    fun invalid(reason: String): Nothing = throw GradleException(
        "Invalid Gradle property '$linkuVersionCodePropertyName': $reason. " +
            "Use $linkuVersionCodeUsage (for example, -PlinkuVersionCode=28)."
    )

    if (value.isEmpty()) {
        invalid("the value is blank")
    }
    val unsignedValue = value.removePrefix("-")
    if (value.startsWith("-") && unsignedValue.isNotEmpty() &&
        unsignedValue.all { it in '0'..'9' }
    ) {
        invalid("negative values are not allowed")
    }
    if (unsignedValue.count { it == '.' } == 1 &&
        unsignedValue.any { it in '0'..'9' } &&
        unsignedValue.all { it == '.' || it in '0'..'9' }
    ) {
        invalid("fractional values are not allowed")
    }
    if (value.any { it !in '0'..'9' }) {
        invalid("the value must contain ASCII decimal digits only")
    }

    val parsedValue = value.toLongOrNull()
        ?: invalid("the value exceeds Google Play's supported versionCode range")

    if (parsedValue == 0L) {
        invalid("zero is not allowed")
    }
    if (parsedValue > maxGooglePlayVersionCode) {
        invalid("the value exceeds Google Play's maximum versionCode of $maxGooglePlayVersionCode")
    }

    return parsedValue.toInt()
}

val releaseVersionCodeProvider = providers.gradleProperty(linkuVersionCodePropertyName)
    .map(::parseLinkuVersionCode)
    .orElse(
        providers.provider<Int> {
            throw GradleException(
                "Required Gradle property '$linkuVersionCodePropertyName' is missing for release builds. " +
                    "Use $linkuVersionCodeUsage (for example, -PlinkuVersionCode=28)."
            )
        }
    )

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

subprojects {
    pluginManager.withPlugin("com.android.application") {
        extensions.configure<ApplicationAndroidComponentsExtension> {
            onVariants(selector().withBuildType("release")) { variant ->
                variant.outputs.forEach { output ->
                    output.versionCode.set(releaseVersionCodeProvider)
                }
            }
        }
    }
}

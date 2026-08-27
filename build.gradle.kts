import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.BuildConfigField
import java.net.URI
import java.util.Properties
import org.gradle.api.provider.Provider

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
val localPropertiesTextProvider = providers
    .fileContents(layout.projectDirectory.file(localPropertiesFile.name))
    .asText
    .orElse("")

fun missingLinkuConfiguration(
    propertyName: String,
    environmentVariableName: String,
): Provider<String> = providers.provider {
    throw GradleException(
        "Required configuration '$propertyName' is missing or blank. " +
            "Set it in local.properties or the $environmentVariableName environment variable."
    )
}

fun validateServerDomain(value: String): String {
    val normalizedValue = value.trim().trimEnd('/')
    val uri = runCatching { URI(normalizedValue) }.getOrNull()
    val isValid = uri != null &&
        uri.isAbsolute &&
        uri.scheme.lowercase() in setOf("http", "https") &&
        !uri.host.isNullOrBlank() &&
        (uri.port == -1 || uri.port in 1..65535) &&
        uri.rawUserInfo == null &&
        uri.rawQuery == null &&
        uri.rawFragment == null

    if (!isValid) {
        throw GradleException(
            "Invalid configuration 'SERVER_DOMAIN': expected an absolute HTTP(S) URL " +
                "with a host, an optional port from 1 to 65535, and without user info, " +
                "query, or fragment."
        )
    }
    return normalizedValue
}

fun validateServerHost(value: String): String {
    val normalizedValue = value.trim()
    val uri = runCatching { URI("https://$normalizedValue") }.getOrNull()
    val isValid = normalizedValue.isNotEmpty() &&
        !normalizedValue.contains("://") &&
        normalizedValue.none { it == '/' || it == '?' || it == '#' || it == '@' || it == ':' } &&
        uri?.host == normalizedValue &&
        uri.rawPath.isNullOrEmpty() &&
        uri.rawQuery == null &&
        uri.rawFragment == null

    if (!isValid) {
        throw GradleException(
            "Invalid configuration 'SERVER_HOST': expected a host name without a scheme, " +
                "port, path, query, fragment, or user info."
        )
    }
    return normalizedValue
}

fun validateApiVersion(value: String): String {
    val normalizedValue = value.trim().trim('/')
    val isValid = normalizedValue.isNotEmpty() &&
        normalizedValue.split('/').all { segment ->
            segment.any { character -> character.isAsciiLetterOrDigit() } &&
                segment.all { character ->
                    character.isAsciiLetterOrDigit() ||
                        character in setOf('-', '.', '_', '~')
                }
        }

    if (!isValid) {
        throw GradleException(
            "Invalid configuration 'API_VERSION': expected one or more non-blank URL path " +
                "segments containing only ASCII letters, digits, '-', '.', '_', or '~'."
        )
    }
    return normalizedValue
}

fun Char.isAsciiLetterOrDigit(): Boolean =
    this in 'a'..'z' || this in 'A'..'Z' || this in '0'..'9'

fun escapeBuildConfigString(value: String): String = buildString {
    value.forEach { character ->
        when (character) {
            '\\' -> append("\\\\")
            '"' -> append("\\\"")
            '\b' -> append("\\b")
            '\t' -> append("\\t")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            else -> if (character.code in 0x00..0x1F || character.code == 0x7F) {
                append("\\u")
                append(character.code.toString(16).padStart(4, '0'))
            } else {
                append(character)
            }
        }
    }
}

val linkuConfigProviders = localPropertyEnvironmentNames.mapValues { (propertyName, environmentVariableName) ->
    val localPropertyProvider = localPropertiesTextProvider
        .map { contents ->
            Properties().apply { contents.reader().use(::load) }
                .getProperty(propertyName)
                ?.trim()
                .orEmpty()
        }
        .filter(String::isNotEmpty)

    val environmentProvider = providers
        .environmentVariable(environmentVariableName)
        .map(String::trim)
        .filter(String::isNotEmpty)

    localPropertyProvider
        .orElse(environmentProvider)
        .orElse(missingLinkuConfiguration(propertyName, environmentVariableName))
        .let { provider ->
            when (propertyName) {
                "SERVER_DOMAIN" -> provider.map(::validateServerDomain)
                "SERVER_HOST" -> provider.map(::validateServerHost)
                "API_VERSION" -> provider.map(::validateApiVersion)
                else -> provider
            }
        }
}

val linkuBuildConfigString: (Provider<String>) -> Provider<BuildConfigField<String>> =
    { valueProvider ->
        valueProvider.map { value ->
            BuildConfigField(
                type = "String",
                value = "\"${escapeBuildConfigString(value)}\"",
                comment = "Generated from an external LinkU configuration provider.",
            )
        }
    }

val linkuManifestValue: (Provider<String>) -> Provider<String> = { valueProvider -> valueProvider }

rootProject.extra["linkuConfigProviders"] = linkuConfigProviders
rootProject.extra["linkuBuildConfigString"] = linkuBuildConfigString
rootProject.extra["linkuManifestValue"] = linkuManifestValue

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

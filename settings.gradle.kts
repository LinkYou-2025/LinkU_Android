pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = java.net.URI(providers.gradleProperty("KAKAO_MAVEN_URL").get()) }
    }
}

rootProject.name = "LinkU_Android"
include(":app")
include(":core")
include(":data")
include(":design")
include(":feature:login")
include(":test:login")
include(":feature:home")
include(":feature:file")
include(":feature:curation")
include(":feature:mypage")
include(":test:curation")
include(":test:mypage")
include(":test:home")
include(":test:file")

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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
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

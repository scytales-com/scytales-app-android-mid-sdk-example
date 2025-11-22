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
        maven {
            url = uri("https://dl.cloudsmith.io/basic/scytales/scytales/maven/")
            credentials {
                username = providers.gradleProperty("repositoryUser").orNull
                password = providers.gradleProperty("repositoryPassword").orNull
            }
        }
    }
}

rootProject.name = "scytales-app-android-mid-sdk-example"
include(":app")
 
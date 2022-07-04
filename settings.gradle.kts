pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://packages.johnturkson.com/maven")
    }
}

rootProject.name = "lambda-sdk"
include(":client")
include(":bootstrap")
include(":infrastructure")
include(":example")

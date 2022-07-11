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

rootProject.name = "aws-lambda-runtime"
include(":client")
include(":events")
include(":bootstrap")
include(":annotations")
include(":cdk")
include(":infrastructure")
include(":example")

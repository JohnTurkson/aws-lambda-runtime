pluginManagement {
    plugins {
        id("org.jetbrains.kotlin.jvm") version "1.7.21" apply false
        id("org.jetbrains.kotlin.plugin.serialization") version "1.7.21" apply false
        id("com.google.devtools.ksp") version "1.7.21-1.0.8" apply false
        id("org.graalvm.buildtools.native") version "0.9.19" apply false
    }
    
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

pluginManagement {
    plugins {
        id("org.jetbrains.kotlin.jvm") version "1.7.21"
        id("org.jetbrains.kotlin.plugin.serialization") version "1.7.21"
        id("com.google.devtools.ksp") version "1.7.21-1.0.8"
        id("com.johnturkson.graalvm") version "1.0.0-SNAPSHOT"
        id("com.johnturkson.toolchain") version "1.0.0-SNAPSHOT"
        id("com.johnturkson.publishing") version "1.0.0-SNAPSHOT"
    }
    
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/JohnTurkson/packages")
            credentials {
                val githubActor: String? by settings
                val githubToken: String? by settings
                username = githubActor ?: System.getenv("GITHUB_ACTOR")
                password = githubToken ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/JohnTurkson/packages")
            credentials {
                val githubActor: String? by settings
                val githubToken: String? by settings
                username = githubActor ?: System.getenv("GITHUB_ACTOR")
                password = githubToken ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

rootProject.name = "aws-lambda-runtime"
include(":annotations")
include(":events")
include(":client")
include(":cdk")
include(":bootstrap")
include(":infrastructure")
include(":example")

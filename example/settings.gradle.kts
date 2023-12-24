pluginManagement {
    plugins {
        id("com.johnturkson.graalvm") version "1.0.0-SNAPSHOT" apply false
        id("com.johnturkson.kotlin.jvm") version "1.0.0-SNAPSHOT" apply false
        id("com.johnturkson.publishing") version "1.0.0-SNAPSHOT" apply false
    }
    
    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/JohnTurkson/packages")
            credentials {
                val githubUsername: String? by settings
                val githubToken: String? by settings
                username = githubUsername ?: System.getenv("GITHUB_USERNAME")
                password = githubToken ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/JohnTurkson/packages")
            credentials {
                val githubUsername: String? by settings
                val githubToken: String? by settings
                username = githubUsername ?: System.getenv("GITHUB_USERNAME")
                password = githubToken ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}


rootProject.name = "aws-lambda-runtime-example"
includeBuild("..")
include(":functions")
include(":cdk")

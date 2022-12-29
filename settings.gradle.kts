pluginManagement {
    plugins {
        id("com.johnturkson.kotlin") version "1.0.0-SNAPSHOT"
        id("com.johnturkson.aws.lambda") version "1.0.0-SNAPSHOT"
        id("com.johnturkson.aws.cdk") version "1.0.0-SNAPSHOT"
        id("com.johnturkson.publishing") version "1.0.0-SNAPSHOT"
    }
    
    repositories {
        gradlePluginPortal()
        google()
        mavenLocal()
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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenLocal()
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

rootProject.name = "aws-lambda-runtime"
include(":annotations")
include(":events")
include(":client")
include(":cdk")
include(":bootstrap")
include(":infrastructure")
include(":example")

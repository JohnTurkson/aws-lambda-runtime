plugins {
    id("org.jetbrains.kotlin.jvm")
    `maven-publish`
}

group = "com.johnturkson.aws.runtime"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.GRAAL_VM)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/JohnTurkson/aws-lambda-runtime")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: project.property("githubActor")?.toString()
                password = System.getenv("GITHUB_TOKEN") ?: project.property("githubToken")?.toString()
            }
        }
    }
}

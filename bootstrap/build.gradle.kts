plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.google.devtools.ksp")
    `maven-publish`
}

group = "com.johnturkson.aws.runtime"
version = "1.0-SNAPSHOT"

dependencies {
    api(project(":annotations"))
    implementation(project(":client"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.21-1.0.8")
}

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
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

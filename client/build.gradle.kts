plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    `maven-publish`
}

group = "com.johnturkson.aws.runtime"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("io.ktor:ktor-client-cio:2.0.3")
    implementation("io.ktor:ktor-client-cio-jvm:2.0.3")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.0-1.0.6")
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
            name = "GitLabPackages"
            url = uri("https://gitlab.com/api/v4/projects/${System.getenv("GITLAB_PROJECT_ID")}/packages/maven")
            authentication {
                create<HttpHeaderAuthentication>("header")
            }
            credentials(HttpHeaderCredentials::class) {
                name = "Deploy-Token"
                value = System.getenv("GITLAB_PUBLISHING_TOKEN")
            }
        }
    }
}

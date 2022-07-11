plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
    `maven-publish`
}

group = "com.johnturkson.aws.runtime"
version = "1.0-SNAPSHOT"

dependencies {
    api(project(":annotations"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.10-1.0.6")
    implementation("com.squareup:kotlinpoet:1.12.0")
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

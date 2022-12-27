plugins {
    id("org.gradle.application")
    id("com.johnturkson.kotlin")
}

application {
    mainClass.set("com.johnturkson.aws.runtime.infrastructure.InfrastructureKt")
}

dependencies {
    implementation(project(":example"))
    implementation("software.amazon.awscdk:aws-cdk-lib:2.55.1")
}

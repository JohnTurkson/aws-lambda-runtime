plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.johnturkson.toolchain")
    application
}

dependencies {
    implementation(project(":example"))
    implementation("software.amazon.awscdk:aws-cdk-lib:2.55.1")
}

application {
    mainClass.set("com.johnturkson.aws.runtime.infrastructure.InfrastructureKt")
}

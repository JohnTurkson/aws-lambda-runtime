plugins {
    id("com.johnturkson.kotlin.jvm")
    id("org.gradle.application")
}

group = "com.johnturkson.aws.lambda.runtime.example"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation(project(":functions"))
    implementation("software.amazon.awscdk:aws-cdk-lib:2.118.0")
}

application {
    mainClass.set("com.johnturkson.aws.runtime.example.cdk.AppKt")
}

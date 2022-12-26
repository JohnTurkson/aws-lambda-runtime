plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.johnturkson.toolchain")
    id("com.johnturkson.publishing")
}

group = "com.johnturkson.aws.runtime"
version = "1.0.0-SNAPSHOT"

dependencies {
    api(project(":annotations"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.21-1.0.8")
    implementation("com.squareup:kotlinpoet:1.12.0")
}

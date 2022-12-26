plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.google.devtools.ksp")
    id("com.johnturkson.graalvm")
    id("com.johnturkson.toolchain")
    id("com.johnturkson.publishing")
}

group = "com.johnturkson.aws.runtime"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation(project(":annotations"))
    implementation(project(":client"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.21-1.0.8")
}

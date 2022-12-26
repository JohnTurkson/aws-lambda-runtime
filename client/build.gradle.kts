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
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("io.ktor:ktor-client-cio:2.2.1")
    implementation("io.ktor:ktor-client-cio-jvm:2.2.1")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.21-1.0.8")
}

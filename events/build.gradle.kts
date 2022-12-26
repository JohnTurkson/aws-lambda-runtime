plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.johnturkson.toolchain")
    id("com.johnturkson.publishing")
}

group = "com.johnturkson.aws.runtime"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
}

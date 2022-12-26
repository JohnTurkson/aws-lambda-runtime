plugins {
    id("org.jetbrains.kotlin.jvm") apply false
    id("org.jetbrains.kotlin.plugin.serialization") apply false
    id("com.google.devtools.ksp") apply false
    id("com.johnturkson.graalvm") apply false
    id("com.johnturkson.toolchain") apply false
    id("com.johnturkson.publishing") apply false
}

group = "com.johnturkson.aws.runtime"
version = "1.0.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.7.20" apply false
    kotlin("plugin.serialization") version "1.7.20" apply false
    id("com.google.devtools.ksp") version "1.7.20-1.0.7" apply false
    id("org.graalvm.buildtools.native") version "0.9.14" apply false
}

group = "com.johnturkson.aws.runtime"
version = "1.0-SNAPSHOT"

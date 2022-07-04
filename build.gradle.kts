plugins {
    kotlin("jvm") version "1.7.0" apply false
    kotlin("plugin.serialization") version "1.7.0" apply false
    id("com.google.devtools.ksp") version "1.7.0-1.0.6" apply false
    id("org.graalvm.buildtools.native") version "0.9.12" apply false
}

group = "com.johnturkson.aws.runtime"
version = "1.0-SNAPSHOT"

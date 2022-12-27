plugins {
    id("com.johnturkson.kotlin")
    id("com.johnturkson.publishing")
}

group = "com.johnturkson.aws.runtime"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation(project(":annotations"))
    implementation("com.squareup:kotlinpoet:1.12.0")
}

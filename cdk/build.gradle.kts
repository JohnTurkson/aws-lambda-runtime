plugins {
    id("com.johnturkson.kotlin.jvm")
    id("com.johnturkson.publishing")
}

group = "com.johnturkson.aws.lambda.runtime"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation(project(":annotations"))
}

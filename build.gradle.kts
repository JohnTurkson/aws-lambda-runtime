plugins {
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.0"
    id("org.graalvm.buildtools.native") version "0.9.12"
    application
}

group = "com.johnturkson.aws"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("io.ktor:ktor-client-cio:2.0.3")
    implementation("io.ktor:ktor-client-cio-jvm:2.0.3")
    implementation("software.amazon.awscdk:aws-cdk-lib:2.30.0")
    implementation(platform("software.amazon.awssdk:bom:2.17.224"))
    implementation("software.amazon.awssdk:url-connection-client")
    implementation("software.amazon.awssdk:dynamodb-enhanced") {
        exclude("software.amazon.awssdk", "netty-nio-client")
        exclude("software.amazon.awssdk", "apache-client")
    }
}

application {
    mainClass.set("InfrastructureKt")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.GRAAL_VM)
    }
}

graalvmNative {
    binaries {
        named("main") {
            verbose.set(true)
            fallback.set(false)
            imageName.set("bootstrap")
            mainClass.set("RuntimeKt")
            buildArgs.add("--enable-url-protocols=http")
            buildArgs.add("--initialize-at-build-time=kotlin,kotlinx")
            buildArgs.add("--initialize-at-build-time=io.ktor")
            buildArgs.add("--initialize-at-build-time=org.slf4j")
        }
    }
}

tasks.register<Zip>("buildLambdaImage") {
    dependsOn("nativeCompile")
    archiveFileName.set("bootstrap.zip")
    destinationDirectory.set(file("$buildDir/lambda/image"))
    from("$buildDir/native/nativeCompile")
}

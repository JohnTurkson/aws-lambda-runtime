plugins {
    kotlin("jvm") version "1.7.0"
    id("org.graalvm.buildtools.native") version "0.9.12"
    application
}

group = "com.johnturkson.aws"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("io.ktor:ktor-client-cio:2.0.3")
    implementation("io.ktor:ktor-client-cio-jvm:2.0.3")
    implementation("software.amazon.awscdk:aws-cdk-lib:2.30.0")
    // implementation(platform("software.amazon.awssdk:bom:2.16.104"))
    // implementation("software.amazon.awssdk:netty-nio-client")
    // implementation("software.amazon.awssdk:dynamodb-enhanced") {
    //     exclude("software.amazon.awssdk", "netty-nio-client")
    //     exclude("software.amazon.awssdk", "apache-client")
    // }
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
            mainClass.set("RuntimeKt")
            buildArgs.add("--enable-url-protocols=http")
            buildArgs.add("--initialize-at-build-time=kotlin,kotlinx,io.ktor")
        }
    }
}

tasks.register<Zip>("buildLambdaImage") {
    dependsOn("nativeCompile")
    dependsOn("buildLambdaBootstrap")
    archiveFileName.set("${project.name}.zip")
    destinationDirectory.set(file("$buildDir/lambda/image"))
    from("$buildDir/native/nativeCompile")
    from("$buildDir/lambda/bootstrap")
}

tasks.register("buildLambdaBootstrap") {
    mkdir("$buildDir/lambda/bootstrap")
    File("$buildDir/lambda/bootstrap", "bootstrap").bufferedWriter().use { writer ->
        writer.write(
            """
            #!/usr/bin/env bash
            
            ./${project.name} ${"$"}_HANDLER
            """.trimIndent()
        )
    }
}

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("org.graalvm.buildtools.native")
    application
}

group = "com.johnturkson.aws.runtime"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":bootstrap"))
    ksp(project(":bootstrap"))
    compileOnly(project(":cdk"))
    ksp(project(":cdk"))
    implementation(project(":client"))
    implementation(project(":events"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    compileOnly("software.amazon.awscdk:aws-cdk-lib:2.46.0")
    implementation(platform("software.amazon.awssdk:bom:2.17.224"))
    implementation("software.amazon.awssdk:url-connection-client")
    implementation("software.amazon.awssdk:dynamodb-enhanced") {
        exclude("software.amazon.awssdk", "netty-nio-client")
        exclude("software.amazon.awssdk", "apache-client")
    }
}

application {
    mainClass.set("com.johnturkson.aws.runtime.generated.bootstrap.BootstrapKt")
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.GRAAL_VM)
    }
}

ksp {
    arg("OUTPUT_LOCATION", "$group.generated")
    arg("HANDLER_LOCATION", "../example/build/lambda/image/bootstrap.zip")
}

graalvmNative {
    binaries {
        named("main") {
            verbose.set(true)
            fallback.set(false)
            imageName.set("bootstrap")
            mainClass.set("com.johnturkson.aws.runtime.generated.bootstrap.BootstrapKt")
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

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("org.graalvm.buildtools.native")
    application
}

dependencies {
    implementation(project(":bootstrap"))
    ksp(project(":bootstrap"))
    implementation(project(":client"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation(platform("software.amazon.awssdk:bom:2.17.224"))
    implementation("software.amazon.awssdk:url-connection-client")
    implementation("software.amazon.awssdk:dynamodb-enhanced") {
        exclude("software.amazon.awssdk", "netty-nio-client")
        exclude("software.amazon.awssdk", "apache-client")
    }
}

application {
    mainClass.set("com.johnturkson.aws.runtime.bootstrap.generated.BootstrapKt")
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }
    }
}

sourceSets {
    main {
        java {
            srcDir("build/generated/ksp/main/kotlin")
        }
    }
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
            mainClass.set("com.johnturkson.aws.runtime.bootstrap.generated.BootstrapKt")
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

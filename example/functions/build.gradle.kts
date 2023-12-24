plugins {
    id("com.johnturkson.graalvm")
    id("com.johnturkson.kotlin.jvm")
    id("org.gradle.idea")
}

group = "com.johnturkson.aws.lambda.runtime.example"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation("com.johnturkson.aws.lambda.runtime:annotations")
    compileOnly("com.johnturkson.aws.lambda.runtime:bootstrap")
    ksp("com.johnturkson.aws.lambda.runtime:bootstrap")
    compileOnly("com.johnturkson.aws.lambda.runtime:cdk")
    ksp("com.johnturkson.aws.lambda.runtime:cdk")
    implementation("com.johnturkson.aws.lambda.runtime:client")
    implementation("com.johnturkson.aws.lambda.runtime:events")
    
    implementation(platform("software.amazon.awssdk:bom:2.19.4"))
    implementation("software.amazon.awssdk:url-connection-client")
    implementation("software.amazon.awssdk:dynamodb-enhanced") {
        exclude("software.amazon.awssdk", "netty-nio-client")
        exclude("software.amazon.awssdk", "apache-client")
    }
    
    compileOnly("software.amazon.awscdk:aws-cdk-lib:2.64.0")
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

idea {
    module {
        generatedSourceDirs.add(file("build/generated/ksp/main/kotlin"))
    }
}

kotlin {
    sourceSets {
        named("main") {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }
    }
}

ksp {
    arg("OUTPUT_PACKAGE", "$group.generated")
    arg("HANDLER_PATH", "${layout.buildDirectory.asFile.get().path.replace('\\', '/')}/native/nativeCompile")
}

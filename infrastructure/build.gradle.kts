plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation("software.amazon.awscdk:aws-cdk-lib:2.30.0")
}

application {
    mainClass.set("com.johnturkson.aws.runtime.infrastructure.InfrastructureKt")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.GRAAL_VM)
    }
}

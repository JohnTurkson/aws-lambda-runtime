plugins {
    id("com.johnturkson.aws.lambda")
    id("com.johnturkson.aws.cdk")
}

group = "com.johnturkson.aws.lambda"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation(platform("software.amazon.awssdk:bom:2.19.4"))
    implementation("software.amazon.awssdk:url-connection-client")
    implementation("software.amazon.awssdk:dynamodb-enhanced") {
        exclude("software.amazon.awssdk", "netty-nio-client")
        exclude("software.amazon.awssdk", "apache-client")
    }
}

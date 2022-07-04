package com.johnturkson.aws.runtime.example

import com.johnturkson.aws.runtime.bootstrap.Function
import com.johnturkson.aws.runtime.client.Handler
import com.johnturkson.aws.runtime.example.UserDefinition.UserTable
import kotlinx.serialization.json.Json
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.awscore.defaultsmode.DefaultsMode
import software.amazon.awssdk.core.SdkSystemSetting
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import kotlin.random.Random

// TODO support top-level functions?
@Function
object ExampleFunction : Handler {
    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    val dynamoDbClient = DynamoDbEnhancedClient.builder()
        .dynamoDbClient(
            DynamoDbClient.builder()
                .defaultsMode(DefaultsMode.IN_REGION)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build()
        )
        .build()
    
    override suspend fun invoke(): String {
        val user = User(UserMetadata(Random.nextLong().toString()))
        dynamoDbClient.UserTable.putItem(user)
        return json.encodeToString(User.serializer(), user)
    }
}

fun main() {

}

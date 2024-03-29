package com.johnturkson.aws.runtime.example.functions

import com.johnturkson.aws.runtime.annotations.Function
import com.johnturkson.aws.runtime.annotations.Memory
import com.johnturkson.aws.runtime.annotations.Timeout
import com.johnturkson.aws.runtime.client.Handler
import com.johnturkson.aws.runtime.client.Request
import com.johnturkson.aws.runtime.events.HttpResponse
import com.johnturkson.aws.runtime.example.functions.UserDefinition.UserTable
import kotlinx.serialization.json.Json
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.core.SdkSystemSetting
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import kotlin.random.Random

val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

val dynamoDbClient = DynamoDbEnhancedClient.builder()
    .dynamoDbClient(
        DynamoDbClient.builder()
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
            .httpClientBuilder(UrlConnectionHttpClient.builder())
            .build()
    )
    .build()

@Function
object ExampleFunction : Handler {
    override suspend fun invoke(request: Request): String {
        val user = User(UserMetadata(Random.nextLong().toString()))
        dynamoDbClient.UserTable.putItem(user)
        return json.encodeToString(User.serializer(), user)
    }
}

@Function
@Timeout(1)
@Memory(1024)
fun ExampleFunction2(request: Request): String {
    return HttpResponse(request.body, 400)
}

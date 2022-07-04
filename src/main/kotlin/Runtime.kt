import example.User
import example.UserDefinition.UserTable
import example.UserMetadata
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.awscore.defaultsmode.DefaultsMode
import software.amazon.awssdk.core.SdkSystemSetting
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import kotlin.random.Random

const val runtimeApiVersion = "2018-06-01"
val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}
val httpClient = HttpClient(CIO) {
    install(HttpTimeout)
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

suspend fun main() {
    val handler = getHandlerName()
    while (true) {
        val request = getNextInvocation()
        val requestId = request.headers["Lambda-Runtime-Aws-Request-Id"]
            ?: error("Missing Lambda-Runtime-Aws-Request-Id")
        val user = User(UserMetadata(Random.nextLong().toString()))
        dynamoDbClient.UserTable.putItem(user)
        sendInvocationResponse(requestId, json.encodeToString(User.serializer(), user))
    }
}

suspend fun getNextInvocation(): HttpResponse {
    val endpoint = getRuntimeEndpoint("runtime/invocation/next")
    return httpClient.get(endpoint) {
        timeout {
            requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
        }
    }
}

suspend fun sendInvocationResponse(requestId: String, data: String): HttpResponse {
    val endpoint = getRuntimeEndpoint("runtime/invocation/$requestId/response")
    return httpClient.post(endpoint) {
        contentType(ContentType.Application.Json)
        setBody(data)
    }
}

suspend fun sendInitializationError(data: String): HttpResponse {
    val endpoint = getRuntimeEndpoint("runtime/init/error")
    return httpClient.post(endpoint) {
        contentType(ContentType.Application.Json)
        setBody(data)
    }
}

fun getRuntimeEndpoint(path: String): String {
    val runtimeApiEndpoint = getLambdaRuntimeApi()
    return "http://$runtimeApiEndpoint/$runtimeApiVersion/${path.trim('/')}"
}

fun getLambdaRuntimeApi(): String {
    return env("AWS_LAMBDA_RUNTIME_API") ?: error("Missing AWS_LAMBDA_RUNTIME_API")
}

fun getHandlerName(): String {
    return env("_HANDLER") ?: error("Missing _HANDLER")
}

fun env(name: String): String? {
    return System.getenv(name)
}

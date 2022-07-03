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

const val runtimeApiVersion = "2018-06-01"
val client = HttpClient(CIO) {
    install(HttpTimeout)
}

suspend fun main() {
    while (true) {
        val request = getNextInvocation()
        val requestId = request.headers["Lambda-Runtime-Aws-Request-Id"] ?: error("Missing Lambda-Runtime-Aws-Request-Id")
        sendInvocationResponse(requestId, "{}")
    }
}

suspend fun getNextInvocation(): HttpResponse {
    val endpoint = getRuntimeEndpoint("runtime/invocation/next")
    return client.get(endpoint) {
        timeout { 
            requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
        }
    }
}

suspend fun sendInvocationResponse(requestId: String, data: String): HttpResponse {
    val endpoint = getRuntimeEndpoint("runtime/invocation/$requestId/response")
    return client.post(endpoint) {
        contentType(ContentType.Application.Json)
        setBody(data)
    }
}

suspend fun sendInitializationError(): HttpResponse {
    val endpoint = getRuntimeEndpoint("runtime/init/error")
    return client.post(endpoint) {
        contentType(ContentType.Application.Json)
    }
}

fun getRuntimeEndpoint(path: String): String {
    val runtimeApiEndpoint = getLambdaRuntimeApi()
    return "http://$runtimeApiEndpoint/$runtimeApiVersion/${path.trim('/')}"
}

fun getLambdaRuntimeApi(): String {
    return env("AWS_LAMBDA_RUNTIME_API") ?: error("Missing AWS_LAMBDA_RUNTIME_API")
}

fun env(name: String): String? {
    return System.getenv(name)
}

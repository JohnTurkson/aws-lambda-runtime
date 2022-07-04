package com.johnturkson.aws.runtime.client

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
val httpClient = HttpClient(CIO) {
    install(HttpTimeout)
}

suspend fun listen(handler: Handler) {
    while (true) {
        val request = getNextInvocation()
        val requestId = request.headers["Lambda-Runtime-Aws-Request-Id"]
            ?: error("Missing Lambda-Runtime-Aws-Request-Id")
        val response = handler()
        sendInvocationResponse(requestId, response)
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

package com.johnturkson.aws.runtime.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class Runtime(private val runtimeApiVersion: String = "2018-06-01") {
    private val httpClient: HttpClient = HttpClient(CIO) { install(HttpTimeout) }
    private val runtimeApi by lazy { env("AWS_LAMBDA_RUNTIME_API") ?: error("Missing AWS_LAMBDA_RUNTIME_API") }
    val handlerName by lazy { env("_HANDLER") ?: error("Missing _HANDLER") }
    
    suspend fun listen(handler: Handler) {
        while (true) {
            val request = getNextInvocation()
            val response = handler(request)
            sendInvocationResponse(request.id, response)
        }
    }
    
    private suspend fun getNextInvocation(): Request {
        val endpoint = getRuntimeEndpoint("runtime/invocation/next")
        val invocation = httpClient.get(endpoint) {
            timeout {
                requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
            }
        }
        val id = getRequestId(invocation)
        val body = getRequestBody(invocation)
        return Request(id, body)
    }
    
    private suspend fun sendInvocationResponse(requestId: String, data: String) {
        val endpoint = getRuntimeEndpoint("runtime/invocation/$requestId/response")
        httpClient.post(endpoint) {
            contentType(ContentType.Application.Json)
            setBody(data)
        }
    }
    
    private suspend fun sendInitializationError(data: String) {
        val endpoint = getRuntimeEndpoint("runtime/init/error")
        httpClient.post(endpoint) {
            contentType(ContentType.Application.Json)
            setBody(data)
        }
    }
    
    private fun getRuntimeEndpoint(path: String): String {
        return "http://$runtimeApi/$runtimeApiVersion/${path.trim('/')}"
    }
    
    private fun getRequestId(invocation: HttpResponse): String {
        return invocation.headers["Lambda-Runtime-Aws-Request-Id"] ?: error("Missing Lambda-Runtime-Aws-Request-Id")
    }
    
    private suspend fun getRequestBody(invocation: HttpResponse): String {
        return invocation.bodyAsText()
    }
    
    private fun env(name: String): String? {
        return System.getenv(name)
    }
}

package com.johnturkson.aws.runtime.events

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

@Serializable
private data class HttpResponseData(
    val body: String,
    val statusCode: Int,
    val isBase64Encoded: Boolean,
    val headers: Map<String, String>,
    val cookies: List<String>,
)

fun HttpResponse(
    body: String,
    statusCode: Int = 200,
    isBase64Encoded: Boolean = false,
    headers: Map<String, String> = mapOf("content-type" to "application/json"),
    cookies: List<String> = emptyList(),
): String {
    val response = HttpResponseData(body, statusCode, isBase64Encoded, headers, cookies)
    return json.encodeToString(HttpResponseData.serializer(), response)
}

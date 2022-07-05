package com.johnturkson.aws.runtime.events

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

@Serializable
private data class ApiResponseData(
    val body: String,
    val statusCode: Int,
    val isBase64Encoded: Boolean,
    val headers: Map<String, String>,
)

fun ApiResponse(
    body: String,
    statusCode: Int,
    isBase64Encoded: Boolean = false,
    headers: Map<String, String> = emptyMap(),
): String {
    val response = ApiResponseData(body, statusCode, isBase64Encoded, headers)
    return json.encodeToString(ApiResponseData.serializer(), response)
}

package com.johnturkson.aws.runtime.client

fun interface Handler {
    suspend operator fun invoke(request: Request): String
}

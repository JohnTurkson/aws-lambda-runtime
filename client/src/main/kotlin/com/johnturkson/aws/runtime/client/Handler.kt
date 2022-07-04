package com.johnturkson.aws.runtime.client

interface Handler {
    suspend operator fun invoke(): String
}

package com.rdapps.weddinginvitation.util

import androidx.compose.runtime.compositionLocalOf
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
    explicitNulls = false
    coerceInputValues = true
}

fun createHttpClient(json: Json = Json) = HttpClient {
    install(ContentNegotiation) {
        json(json)
    }
}

val LocalUserId = compositionLocalOf<String?> { null }
package com.example.flushcards.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

object TranslationService {

    private val client = HttpClient()
    private const val GOOGLE_TRANSLATE_URL = "https://translate.googleapis.com/translate_a/single"

    suspend fun translate(text: String): String = withContext(Dispatchers.IO) {
        if (text.isBlank()) return@withContext ""

        runCatching {
            val response = client.get(GOOGLE_TRANSLATE_URL) {
                header("User-Agent", "Mozilla/5.0")
                parameter("client", "gtx")
                parameter("sl", "en")
                parameter("tl", "ru")
                parameter("dt", "t")
                parameter("q", text)
            }

            if (response.status == HttpStatusCode.OK) {
                Json.parseToJsonElement(response.bodyAsText())
                    .jsonArray[0].jsonArray
                    .joinToString("") { it.jsonArray[0].jsonPrimitive.content }
            } else ""
        }.getOrDefault("")
    }
}
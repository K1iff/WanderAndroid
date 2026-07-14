package com.example.wander.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object WanderClient {
    // Replace with your laptop's Wi-Fi IP address if you are running on a real phone.
    const val BASE_URL = "http://192.168.100.3/wander_api/"

    val httpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Prevents crashes if PHP returns unmapped columns
                prettyPrint = true
                isLenient = true
            })
        }
    }
}
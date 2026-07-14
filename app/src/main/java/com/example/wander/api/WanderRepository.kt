package com.example.wander.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class WanderRepository {

    private val client = WanderClient.httpClient
    private val baseUrl = WanderClient.BASE_URL

    suspend fun login(request: LoginRequest): LoginResponse {
        return client.post("${baseUrl}login.php") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    suspend fun getDestinations(): DestinationsResponse {
        return client.get("${baseUrl}get_destinations.php").body()
    }
    suspend fun saveToItinerary(request: SaveItineraryRequest): SaveItineraryResponse {
        return client.post("${baseUrl}save_itinerary.php") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    suspend fun getUserItinerary(userId: Int): ItineraryListResponse {
        return client.get("${baseUrl}get_itinerary.php") {
            url { parameters.append("user_id", userId.toString()) }
        }.body()
    }
    suspend fun registerUser(request: RegisterRequest): RegisterResponse {
        return client.post("${baseUrl}register.php") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    suspend fun getUserStats(userId: Int): StatsResponse {
        return client.get("${baseUrl}get_stats.php") {
            url { parameters.append("user_id", userId.toString()) }
        }.body()
    }
    suspend fun deleteItinerary(request: DeleteItineraryRequest): DeleteItineraryResponse {
        return client.post("${baseUrl}delete_itinerary.php") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    suspend fun updateProfile(request: UpdateProfileRequest): UpdateProfileResponse {
        return client.post("${baseUrl}update_profile.php") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    suspend fun getUserProfile(userId: Int): UserProfileResponse {
        return client.get("${baseUrl}get_profile.php") {
            url { parameters.append("user_id", userId.toString()) }
        }.body()
    }
}
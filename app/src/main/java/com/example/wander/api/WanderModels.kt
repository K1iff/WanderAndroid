package com.example.wander.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- AUTHENTICATION ---
@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(
    val status: String,
    val message: String,
    @SerialName("user_id") val userId: Int? = null,
    @SerialName("full_name") val fullName: String? = null
)

// --- DESTINATIONS ---
@Serializable
data class Destination(
    @SerialName("destination_id") val destinationId: Int,
    val name: String,
    val location: String,
    val province: String,
    val description: String,
    @SerialName("opening_hours") val openingHours: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("cat_culture_heritage") val catCultureHeritage: Int = 0,
    @SerialName("cat_nature_outdoors") val catNatureOutdoors: Int = 0
)

@Serializable
data class DestinationsResponse(
    val status: String,
    val data: List<Destination> = emptyList(),
    val message: String? = null
)

// --- ITINERARIES ---
@Serializable
data class SaveItineraryRequest(
    @SerialName("user_id") val userId: Int,
    @SerialName("destination_id") val destinationId: Int,
    @SerialName("day_number") val dayNumber: Int,
    @SerialName("scheduled_date") val scheduledDate: String, // format: "YYYY-MM-DD"
    @SerialName("scheduled_time") val scheduledTime: String  // format: "HH:MM:SS"
)

@Serializable
data class Suggestion(
    @SerialName("destination_id") val destinationId: Int,
    val name: String,
    val location: String,
    val province: String
)

@Serializable
data class SaveItineraryResponse(
    val status: String,
    val message: String,
    val suggestion: Suggestion? = null
)

@Serializable
data class UserItineraryItem(
    @SerialName("itinerary_id") val itineraryId: Int,
    @SerialName("day_number") val dayNumber: Int,
    @SerialName("scheduled_date") val scheduledDate: String,
    @SerialName("scheduled_time") val scheduledTime: String,
    val name: String,
    val location: String,
    val province: String,
    @SerialName("image_url") val imageUrl: String? = null
)

@Serializable
data class RegisterRequest(
    @SerialName("full_name") val fullName: String,
    @SerialName("birthdate") val birthdate: String,
    @SerialName("sex") val sex: String,
    @SerialName("location") val location: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class RegisterResponse(
    val status: String,
    val message: String
)
@Serializable
data class ItineraryListResponse(
    val status: String,
    val data: List<UserItineraryItem> = emptyList(),
    val message: String? = null
)

@Serializable
data class ItineraryResponse(
    val status: String,
    val message: String,
    val data: List<ItineraryItem> = emptyList()
)
@Serializable
data class ItineraryItem(
    @SerialName("itinerary_id") val itineraryId: Int = 0,
    @SerialName("destination_name") val destinationName: String = "Unknown",
    @SerialName("location") val location: String = "Unknown Location",
    @SerialName("day_number") val dayNumber: Int = 1,
    @SerialName("scheduled_date") val scheduledDate: String = "2026-01-01", // ADD THIS
    @SerialName("scheduled_time") val scheduledTime: String = "08:00:00"
)@Serializable
data class StatsResponse(
    val status: String,
    val message: String? = null,
    @SerialName("saved_count") val savedCount: Int = 0,
    @SerialName("places_visited") val placesVisited: Int = 0
)
@Serializable
data class DeleteItineraryRequest(
    @SerialName("itinerary_id") val itineraryId: Int
)

@Serializable
data class DeleteItineraryResponse(
    val status: String
)

@Serializable
data class UpdateProfileRequest(
    @SerialName("user_id") val userId: Int,
    @SerialName("full_name") val fullName: String,
    val birthdate: String,
    val sex: String,
    val location: String,
    val email: String,
    val password: String
)

@Serializable
data class UpdateProfileResponse(
    val status: String,
    val message: String
)

@Serializable
data class UserProfileData(
    @SerialName("full_name") val fullName: String,
    val birthdate: String,
    val sex: String,
    val location: String,
    val email: String
)

@Serializable
data class UserProfileResponse(
    val status: String,
    val data: UserProfileData? = null,
    val message: String? = null
)
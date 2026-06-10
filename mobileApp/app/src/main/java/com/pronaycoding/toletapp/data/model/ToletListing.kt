package com.pronaycoding.toletapp.data.model

data class ToletListing(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val locationLower: String = "",
    val price: String = "",
    val bedrooms: String = "",
    val propertyType: String = "",
    val images: List<String> = emptyList(),
    val createdAt: Long = 0L,
) {
    fun toMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "userName" to userName,
        "title" to title,
        "description" to description,
        "location" to location,
        "locationLower" to locationLower,
        "price" to price,
        "bedrooms" to bedrooms,
        "propertyType" to propertyType,
        "images" to images,
        "createdAt" to createdAt,
    )

    companion object {
        private fun parseImages(data: Map<String, Any?>): List<String> {
            val value = data["images"] ?: data["imageUrls"]
            return when (value) {
                is List<*> -> value.filterIsInstance<String>()
                is Map<*, *> -> value.entries
                    .sortedBy { it.key.toString().toIntOrNull() ?: 0 }
                    .mapNotNull { it.value as? String }
                else -> emptyList()
            }
        }

        fun fromMap(id: String, data: Map<String, Any?>): ToletListing {
            return ToletListing(
                id = id,
                userId = data["userId"] as? String ?: "",
                userName = data["userName"] as? String ?: "",
                title = data["title"] as? String ?: "",
                description = data["description"] as? String ?: "",
                location = data["location"] as? String ?: "",
                locationLower = data["locationLower"] as? String ?: "",
                price = data["price"] as? String ?: "",
                bedrooms = data["bedrooms"] as? String ?: "",
                propertyType = data["propertyType"] as? String ?: "",
                images = parseImages(data),
                createdAt = when (val value = data["createdAt"]) {
                    is Long -> value
                    is Number -> value.toLong()
                    else -> 0L
                },
            )
        }
    }
}

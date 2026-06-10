package com.pronaycoding.toletapp.data.model

data class UserProfile(
    val userId: String = "",
    val displayName: String = "",
    val phoneNumber: String = "",
    val photoUrl: String = "",
) {
    fun toMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "displayName" to displayName,
        "phoneNumber" to phoneNumber,
        "photoUrl" to photoUrl,
    )

    companion object {
        fun fromMap(data: Map<String, Any?>): UserProfile {
            return UserProfile(
                userId = data["userId"] as? String ?: "",
                displayName = data["displayName"] as? String ?: "",
                phoneNumber = data["phoneNumber"] as? String ?: "",
                photoUrl = data["photoUrl"] as? String ?: "",
            )
        }
    }
}

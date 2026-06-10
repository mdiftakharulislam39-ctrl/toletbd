package com.pronaycoding.toletapp.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.pronaycoding.toletapp.data.model.UserProfile

interface UserRepository {
    suspend fun hasPhoneNumber(userId: String): Boolean

    suspend fun savePhoneNumber(user: FirebaseUser, phoneNumber: String): Result<Unit>

    suspend fun findUserByPhone(phone: String): Result<UserProfile?>

    suspend fun getUserProfile(userId: String): Result<UserProfile>

    suspend fun saveListing(userId: String, listingId: String): Result<Unit>

    suspend fun unsaveListing(userId: String, listingId: String): Result<Unit>

    suspend fun isListingSaved(userId: String, listingId: String): Boolean

    suspend fun getSavedListingIds(userId: String): Result<List<String>>

    companion object {
        fun isValidPhone(phone: String): Boolean {
            val digits = phone.filter { it.isDigit() }
            return digits.length in 10..14
        }
    }
}

package com.pronaycoding.toletapp.data

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.pronaycoding.toletapp.data.model.UserProfile
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val database: FirebaseDatabase = run {
        val url = FirebaseApp.getInstance().options.databaseUrl
        if (!url.isNullOrBlank()) {
            FirebaseDatabase.getInstance(url)
        } else {
            FirebaseDatabase.getInstance("https://toletapp-6eb8e-default-rtdb.firebaseio.com")
        }
    },
) {
    private fun usersRef(userId: String) = database.getReference("users").child(userId)
    private fun savedRef(userId: String) = database.getReference("savedTolets").child(userId)

    suspend fun hasPhoneNumber(userId: String): Boolean {
        return try {
            val snapshot = usersRef(userId).child("phoneNumber").get().await()
            !snapshot.getValue(String::class.java).isNullOrBlank()
        } catch (_: Exception) {
            false
        }
    }

    suspend fun savePhoneNumber(user: FirebaseUser, phoneNumber: String): Result<Unit> {
        return try {
            val profile = UserProfile(
                userId = user.uid,
                displayName = user.displayName.orEmpty(),
                phoneNumber = phoneNumber.trim(),
                photoUrl = user.photoUrl?.toString().orEmpty(),
            )
            usersRef(user.uid).setValue(profile.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun findUserByPhone(phone: String): Result<UserProfile?> {
        return try {
            val normalized = phone.filter { it.isDigit() }
            if (normalized.isBlank()) {
                return Result.success(null)
            }
            val snapshot = database.getReference("users").get().await()
            val match = snapshot.children.firstOrNull { child ->
                val data = child.children.associate { it.key!! to it.value }
                val profile = UserProfile.fromMap(data)
                profile.phoneNumber.filter { it.isDigit() } == normalized
            }?.let { child ->
                val data = child.children.associate { it.key!! to it.value }
                UserProfile.fromMap(data)
            }
            Result.success(match)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(userId: String): Result<UserProfile> {
        return try {
            val snapshot = usersRef(userId).get().await()
            val data = snapshot.children.associate { it.key!! to it.value }
            if (data.isEmpty()) {
                Result.failure(IllegalStateException("User profile not found."))
            } else {
                Result.success(UserProfile.fromMap(data))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveListing(userId: String, listingId: String): Result<Unit> {
        return try {
            savedRef(userId).child(listingId).setValue(System.currentTimeMillis()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unsaveListing(userId: String, listingId: String): Result<Unit> {
        return try {
            savedRef(userId).child(listingId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isListingSaved(userId: String, listingId: String): Boolean {
        return try {
            val snapshot = savedRef(userId).child(listingId).get().await()
            snapshot.exists()
        } catch (_: Exception) {
            false
        }
    }

    suspend fun getSavedListingIds(userId: String): Result<List<String>> {
        return try {
            val snapshot = savedRef(userId).get().await()
            val ids = snapshot.children.mapNotNull { it.key }
            Result.success(ids)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        fun isValidPhone(phone: String): Boolean {
            val digits = phone.filter { it.isDigit() }
            return digits.length in 10..14
        }
    }
}

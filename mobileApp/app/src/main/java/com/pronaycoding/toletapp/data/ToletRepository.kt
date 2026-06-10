package com.pronaycoding.toletapp.data

import android.content.Context
import android.net.Uri
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.pronaycoding.toletapp.data.model.ToletListing
import kotlinx.coroutines.tasks.await

class ToletRepository(
    private val database: FirebaseDatabase = run {
        val url = FirebaseApp.getInstance().options.databaseUrl
        if (!url.isNullOrBlank()) {
            FirebaseDatabase.getInstance(url)
        } else {
            FirebaseDatabase.getInstance("https://toletapp-6eb8e-default-rtdb.firebaseio.com")
        }
    },
) {
    private val listingsRef get() = database.getReference(PATH)

    suspend fun createListing(
        context: Context,
        listing: ToletListing,
        imageUris: List<Uri>,
    ): Result<String> {
        if (imageUris.size > MAX_IMAGES) {
            return Result.failure(IllegalArgumentException("Maximum $MAX_IMAGES images allowed."))
        }

        return try {
            val listingRef = listingsRef.push()
            val listingId = listingRef.key
                ?: return Result.failure(IllegalStateException("Could not create listing id."))

            val images = encodeImages(context, imageUris)
            val listingWithImages = listing.copy(
                id = listingId,
                images = images,
            )
            listingRef.setValue(listingWithImages.toMap()).await()
            Result.success(listingId)
        } catch (e: Exception) {
            Result.failure(Exception(mapErrorMessage(e), e))
        }
    }

    suspend fun searchByLocation(query: String): Result<List<ToletListing>> {
        val trimmed = query.trim()
        return fetchAllListings().map { listings ->
            if (trimmed.isBlank()) {
                listings
            } else {
                val searchTerm = trimmed.lowercase()
                listings.filter { listing ->
                    listing.locationLower.contains(searchTerm) ||
                        listing.location.lowercase().contains(searchTerm)
                }
            }
        }
    }

    suspend fun getAllListings(): Result<List<ToletListing>> = fetchAllListings()

    suspend fun getListingById(listingId: String): Result<ToletListing> {
        return try {
            val snapshot = listingsRef.child(listingId).get().await()
            if (!snapshot.exists()) {
                Result.failure(IllegalStateException("Listing not found."))
            } else {
                val data = snapshot.children.associate { it.key!! to it.value }
                Result.success(ToletListing.fromMap(snapshot.key!!, data))
            }
        } catch (e: Exception) {
            Result.failure(Exception(mapErrorMessage(e), e))
        }
    }

    suspend fun getListingsByIds(ids: List<String>): Result<List<ToletListing>> {
        return fetchAllListings().map { listings ->
            val idSet = ids.toSet()
            listings.filter { it.id in idSet }
        }
    }

    suspend fun getListingsByUserId(userId: String): Result<List<ToletListing>> {
        return fetchAllListings().map { listings ->
            listings.filter { it.userId == userId }
        }
    }

    suspend fun updateListing(
        context: Context,
        listing: ToletListing,
        newImageUris: List<Uri>,
    ): Result<Unit> {
        if (listing.images.size + newImageUris.size > MAX_IMAGES) {
            return Result.failure(IllegalArgumentException("Maximum $MAX_IMAGES images allowed."))
        }

        return try {
            val newImages = encodeImages(context, newImageUris)
            val updatedListing = listing.copy(images = listing.images + newImages)
            listingsRef.child(listing.id).setValue(updatedListing.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapErrorMessage(e), e))
        }
    }

    suspend fun deleteListing(listingId: String): Result<Unit> {
        return try {
            listingsRef.child(listingId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapErrorMessage(e), e))
        }
    }

    private suspend fun fetchAllListings(): Result<List<ToletListing>> {
        return try {
            val snapshot = listingsRef.get().await()
            val listings = snapshot.toListings()
                .sortedByDescending { it.createdAt }
            Result.success(listings)
        } catch (e: Exception) {
            Result.failure(Exception(mapErrorMessage(e), e))
        }
    }

    private suspend fun encodeImages(context: Context, imageUris: List<Uri>): List<String> {
        return imageUris.map { uri ->
            ImageEncoder.encodeToBase64(context, uri)
        }
    }

    private fun DataSnapshot.toListings(): List<ToletListing> {
        return children.mapNotNull { child ->
            val data = child.children.associate { it.key!! to it.value }
            ToletListing.fromMap(child.key!!, data)
        }
    }

    private fun mapErrorMessage(e: Exception): String {
        val message = e.message.orEmpty()
        return when {
            message.contains("PERMISSION_DENIED", ignoreCase = true) ||
                message.contains("permission_denied", ignoreCase = true) ->
                "Permission denied. Check Realtime Database rules."

            else -> message.ifBlank { "Failed to load listings." }
        }
    }

    companion object {
        private const val PATH = "tolets"
        const val MAX_IMAGES = 2
    }
}

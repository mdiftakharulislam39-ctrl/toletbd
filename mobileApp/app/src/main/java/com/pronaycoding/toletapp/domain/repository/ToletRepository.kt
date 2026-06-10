package com.pronaycoding.toletapp.domain.repository

import android.content.Context
import android.net.Uri
import com.pronaycoding.toletapp.data.model.ToletListing

interface ToletRepository {
    suspend fun createListing(
        context: Context,
        listing: ToletListing,
        imageUris: List<Uri>,
    ): Result<String>

    suspend fun searchByLocation(query: String): Result<List<ToletListing>>

    suspend fun getAllListings(): Result<List<ToletListing>>

    suspend fun getListingById(listingId: String): Result<ToletListing>

    suspend fun getListingsByIds(ids: List<String>): Result<List<ToletListing>>

    suspend fun getListingsByUserId(userId: String): Result<List<ToletListing>>

    suspend fun updateListing(
        context: Context,
        listing: ToletListing,
        newImageUris: List<Uri>,
    ): Result<Unit>

    suspend fun deleteListing(listingId: String): Result<Unit>

    companion object {
        const val MAX_IMAGES = 2
    }
}

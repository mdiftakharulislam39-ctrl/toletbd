package com.pronaycoding.toletapp.data.repository

import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.pronaycoding.toletapp.domain.repository.AccountRepository
import com.pronaycoding.toletapp.domain.repository.ToletRepository
import kotlinx.coroutines.tasks.await

class AccountRepositoryImpl(
    private val database: FirebaseDatabase,
    private val toletRepository: ToletRepository,
) : AccountRepository {
    override suspend fun deleteAccount(user: FirebaseUser): Result<Unit> {
        return try {
            deleteAllUserData(user.uid)
            user.delete().await()
            Result.success(Unit)
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            Result.failure(
                IllegalStateException(
                    "Please confirm your Google account when prompted, then try again.",
                    e,
                ),
            )
        } catch (e: Exception) {
            Result.failure(Exception(mapErrorMessage(e), e))
        }
    }

    private suspend fun deleteAllUserData(userId: String) {
        deleteUserListings(userId)
        deleteUserChats(userId)
        deleteUserChatIndex(userId)
        removeNode("savedTolets/$userId")
        removeNode("users/$userId")
    }

    private suspend fun deleteUserChatIndex(userId: String) {
        val snapshot = database.getReference("userChats").child(userId).get().await()
        snapshot.children.forEach { child ->
            child.key?.let { chatId ->
                database.getReference("userChats").child(userId).child(chatId).removeValue().await()
            }
        }
    }

    private suspend fun removeNode(path: String) {
        database.getReference(path).removeValue().await()
    }

    private suspend fun deleteUserListings(userId: String) {
        val listings = toletRepository.getListingsByUserId(userId).getOrElse { emptyList() }
        listings.forEach { listing ->
            toletRepository.deleteListing(listing.id).getOrElse { error ->
                throw IllegalStateException("Failed to delete listing ${listing.id}: ${error.message}", error)
            }
        }
    }

    private suspend fun deleteUserChats(userId: String) {
        val chatIdsSnapshot = database.getReference("userChats").child(userId).get().await()
        for (chatIdChild in chatIdsSnapshot.children) {
            val chatId = chatIdChild.key ?: continue
            val chatSnapshot = database.getReference("chats").child(chatId).get().await()
            if (chatSnapshot.exists()) {
                val otherParticipantIds = chatSnapshot.child("participants").children
                    .mapNotNull { it.key }
                    .filter { it != userId }

                database.getReference("chats").child(chatId).child("messages").removeValue().await()
                database.getReference("chats").child(chatId).child("participants").removeValue().await()
                otherParticipantIds.forEach { otherUserId ->
                    database.getReference("userChats")
                        .child(otherUserId)
                        .child(chatId)
                        .removeValue()
                        .await()
                }
            }
        }
    }

    private fun mapErrorMessage(e: Exception): String {
        val message = e.message.orEmpty()
        return when {
            message.contains("PERMISSION_DENIED", ignoreCase = true) ||
                message.contains("permission_denied", ignoreCase = true) ->
                "Database permission denied. Publish the latest database.rules.json in Firebase Console."

            message.contains("CREDENTIAL", ignoreCase = true) ||
                message.contains("cancel", ignoreCase = true) ->
                "Google confirmation was cancelled. Account was not deleted."

            else -> message.ifBlank { "Failed to delete account." }
        }
    }
}

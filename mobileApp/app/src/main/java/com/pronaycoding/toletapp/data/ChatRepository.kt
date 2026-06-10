package com.pronaycoding.toletapp.data

import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.pronaycoding.toletapp.data.model.ChatConversation
import com.pronaycoding.toletapp.data.model.ChatMessage
import kotlinx.coroutines.tasks.await

class ChatRepository(
    private val database: FirebaseDatabase = run {
        val url = FirebaseApp.getInstance().options.databaseUrl
        if (!url.isNullOrBlank()) {
            FirebaseDatabase.getInstance(url)
        } else {
            FirebaseDatabase.getInstance("https://toletapp-6eb8e-default-rtdb.firebaseio.com")
        }
    },
) {
    suspend fun getMessages(currentUserId: String, otherUserId: String): Result<List<ChatMessage>> {
        return try {
            val id = chatId(currentUserId, otherUserId)
            ensureUserChatIndex(currentUserId, otherUserId, id)
            val snapshot = database.getReference("chats").child(id).child("messages").get().await()
            val messages = snapshot.children.mapNotNull { child ->
                val data = child.children.associate { it.key!! to it.value }
                ChatMessage.fromMap(child.key!!, data)
            }.sortedBy { it.timestamp }
            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(Exception(mapErrorMessage(e), e))
        }
    }

    suspend fun sendMessage(
        currentUserId: String,
        otherUserId: String,
        text: String,
    ): Result<Unit> {
        return try {
            val id = chatId(currentUserId, otherUserId)
            val chatRef = database.getReference("chats").child(id)
            chatRef.child("participants").child(currentUserId).setValue(true).await()
            chatRef.child("participants").child(otherUserId).setValue(true).await()
            ensureUserChatIndex(currentUserId, otherUserId, id)

            val messageRef = chatRef.child("messages").push()
            val message = ChatMessage(
                id = messageRef.key.orEmpty(),
                senderId = currentUserId,
                text = text.trim(),
                timestamp = System.currentTimeMillis(),
            )
            messageRef.setValue(message.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapErrorMessage(e), e))
        }
    }

    suspend fun getConversations(currentUserId: String): Result<List<ChatConversation>> {
        return try {
            val chatIdsSnapshot = database.getReference("userChats")
                .child(currentUserId)
                .get()
                .await()
            val conversations = chatIdsSnapshot.children.mapNotNull { chatIdChild ->
                val id = chatIdChild.key ?: return@mapNotNull null
                val chatSnapshot = database.getReference("chats").child(id).get().await()
                if (!chatSnapshot.exists()) return@mapNotNull null

                val participantIds = chatSnapshot.child("participants").children.mapNotNull { it.key }
                val otherUserId = participantIds.firstOrNull { it != currentUserId } ?: return@mapNotNull null

                var lastMessage: ChatMessage? = null
                for (msgChild in chatSnapshot.child("messages").children) {
                    val data = msgChild.children.associate { it.key!! to it.value }
                    val message = ChatMessage.fromMap(msgChild.key!!, data)
                    if (lastMessage == null || message.timestamp > lastMessage.timestamp) {
                        lastMessage = message
                    }
                }

                ChatConversation(otherUserId = otherUserId, lastMessage = lastMessage)
            }.sortedByDescending { it.lastMessage?.timestamp ?: 0L }
            Result.success(conversations)
        } catch (e: Exception) {
            Result.failure(Exception(mapErrorMessage(e), e))
        }
    }

    fun chatId(userId1: String, userId2: String): String {
        return listOf(userId1, userId2).sorted().joinToString("_")
    }

    private suspend fun ensureUserChatIndex(
        currentUserId: String,
        otherUserId: String,
        id: String,
    ) {
        database.getReference("userChats").child(currentUserId).child(id).setValue(true).await()
        database.getReference("userChats").child(otherUserId).child(id).setValue(true).await()
    }

    private fun mapErrorMessage(e: Exception): String {
        val message = e.message.orEmpty()
        return when {
            message.contains("PERMISSION_DENIED", ignoreCase = true) ||
                message.contains("permission_denied", ignoreCase = true) ->
                "Permission denied. Add chat rules in Firebase Console (see database.rules.json)."

            else -> message.ifBlank { "Chat request failed." }
        }
    }
}

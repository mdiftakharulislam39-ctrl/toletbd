package com.pronaycoding.toletapp.data

import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
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
            val chatId = chatId(currentUserId, otherUserId)
            val snapshot = database.getReference("chats").child(chatId).child("messages").get().await()
            val messages = snapshot.children.mapNotNull { child ->
                val data = child.children.associate { it.key!! to it.value }
                ChatMessage.fromMap(child.key!!, data)
            }.sortedBy { it.timestamp }
            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(
        currentUserId: String,
        otherUserId: String,
        text: String,
    ): Result<Unit> {
        return try {
            val chatId = chatId(currentUserId, otherUserId)
            val chatRef = database.getReference("chats").child(chatId)
            chatRef.child("participants").child(currentUserId).setValue(true).await()
            chatRef.child("participants").child(otherUserId).setValue(true).await()

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
            Result.failure(e)
        }
    }

    fun chatId(userId1: String, userId2: String): String {
        return listOf(userId1, userId2).sorted().joinToString("_")
    }
}

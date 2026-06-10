package com.pronaycoding.toletapp.domain.repository

import com.pronaycoding.toletapp.data.model.ChatConversation
import com.pronaycoding.toletapp.data.model.ChatMessage

interface ChatRepository {
    suspend fun getMessages(currentUserId: String, otherUserId: String): Result<List<ChatMessage>>

    suspend fun sendMessage(
        currentUserId: String,
        otherUserId: String,
        text: String,
    ): Result<Unit>

    suspend fun getConversations(currentUserId: String): Result<List<ChatConversation>>

    fun chatId(userId1: String, userId2: String): String
}

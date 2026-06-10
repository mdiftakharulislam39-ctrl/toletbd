package com.pronaycoding.toletapp.data.model

data class ChatConversation(
    val otherUserId: String,
    val otherUser: UserProfile? = null,
    val lastMessage: ChatMessage? = null,
)

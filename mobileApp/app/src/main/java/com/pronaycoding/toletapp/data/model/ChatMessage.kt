package com.pronaycoding.toletapp.data.model

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
) {
    fun toMap(): Map<String, Any> = mapOf(
        "senderId" to senderId,
        "text" to text,
        "timestamp" to timestamp,
    )

    companion object {
        fun fromMap(id: String, data: Map<String, Any?>): ChatMessage {
            return ChatMessage(
                id = id,
                senderId = data["senderId"] as? String ?: "",
                text = data["text"] as? String ?: "",
                timestamp = when (val value = data["timestamp"]) {
                    is Long -> value
                    is Number -> value.toLong()
                    else -> 0L
                },
            )
        }
    }
}

package com.pronaycoding.toletapp.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pronaycoding.toletapp.data.model.ChatMessage
import com.pronaycoding.toletapp.data.model.UserProfile
import com.pronaycoding.toletapp.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val messageText: String = "",
    val isLoading: Boolean = true,
)

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val currentUserId: String,
    private val otherUser: UserProfile,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadMessages(showLoading = true)
    }

    fun onMessageTextChange(text: String) {
        _uiState.update { it.copy(messageText = text) }
    }

    fun sendMessage() {
        val text = _uiState.value.messageText.trim()
        if (text.isBlank()) return

        val pendingId = "pending-${System.currentTimeMillis()}"
        val pendingMessage = ChatMessage(
            id = pendingId,
            senderId = currentUserId,
            text = text,
            timestamp = System.currentTimeMillis(),
        )
        _uiState.update {
            it.copy(
                messages = it.messages + pendingMessage,
                messageText = "",
            )
        }

        viewModelScope.launch {
            chatRepository.sendMessage(
                currentUserId = currentUserId,
                otherUserId = otherUser.userId,
                text = text,
            ).onSuccess {
                loadMessages()
            }.onFailure {
                _uiState.update { state ->
                    state.copy(
                        messages = state.messages.filterNot { it.id == pendingId },
                        messageText = text,
                    )
                }
            }
        }
    }

    private fun loadMessages(showLoading: Boolean = false) {
        viewModelScope.launch {
            if (showLoading) _uiState.update { it.copy(isLoading = true) }
            chatRepository.getMessages(currentUserId, otherUser.userId)
                .onSuccess { messages -> _uiState.update { it.copy(messages = messages) } }
            if (showLoading) _uiState.update { it.copy(isLoading = false) }
        }
    }
}

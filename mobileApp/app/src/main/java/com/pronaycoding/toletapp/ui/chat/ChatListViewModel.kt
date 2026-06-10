package com.pronaycoding.toletapp.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pronaycoding.toletapp.data.model.ChatConversation
import com.pronaycoding.toletapp.data.model.UserProfile
import com.pronaycoding.toletapp.domain.repository.ChatRepository
import com.pronaycoding.toletapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatListUiState(
    val conversations: List<ChatConversation> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class ChatListViewModel(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val userId: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    private fun loadConversations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            chatRepository.getConversations(userId)
                .onSuccess { rawConversations ->
                    val enriched = rawConversations.map { conversation ->
                        val profileResult = userRepository.getUserProfile(conversation.otherUserId)
                        conversation.copy(
                            otherUser = profileResult.getOrNull()
                                ?: UserProfile(
                                    userId = conversation.otherUserId,
                                    displayName = "User",
                                ),
                        )
                    }
                    _uiState.update { it.copy(conversations = enriched) }
                }
                .onFailure { error -> _uiState.update { it.copy(errorMessage = error.message) } }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

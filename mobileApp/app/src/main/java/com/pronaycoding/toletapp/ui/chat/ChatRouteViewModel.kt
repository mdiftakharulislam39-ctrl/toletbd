package com.pronaycoding.toletapp.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pronaycoding.toletapp.data.model.UserProfile
import com.pronaycoding.toletapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatRouteUiState(
    val otherUser: UserProfile? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class ChatRouteViewModel(
    private val userRepository: UserRepository,
    private val otherUserId: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatRouteUiState())
    val uiState: StateFlow<ChatRouteUiState> = _uiState.asStateFlow()

    init {
        loadOtherUser()
    }

    private fun loadOtherUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            userRepository.getUserProfile(otherUserId)
                .onSuccess { profile -> _uiState.update { it.copy(otherUser = profile) } }
                .onFailure {
                    _uiState.update {
                        it.copy(otherUser = UserProfile(userId = otherUserId, displayName = "User"))
                    }
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

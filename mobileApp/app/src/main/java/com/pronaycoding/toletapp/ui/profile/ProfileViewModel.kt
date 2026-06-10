package com.pronaycoding.toletapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pronaycoding.toletapp.data.model.UserProfile
import com.pronaycoding.toletapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val showDeleteDialog: Boolean = false,
    val isDeletingAccount: Boolean = false,
    val deleteErrorMessage: String? = null,
)

class ProfileViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            userRepository.getUserProfile(userId)
                .onSuccess { profile -> _uiState.update { it.copy(profile = profile) } }
                .onFailure { error -> _uiState.update { it.copy(errorMessage = error.message) } }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun showDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = true, deleteErrorMessage = null) }
    }

    fun dismissDeleteDialog() {
        if (!_uiState.value.isDeletingAccount) {
            _uiState.update { it.copy(showDeleteDialog = false) }
        }
    }

    fun deleteAccount(
        onDelete: suspend () -> Result<Unit>,
        onSuccess: () -> Unit,
        onFailureMessage: String,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAccount = true, deleteErrorMessage = null) }
            onDelete()
                .onSuccess {
                    _uiState.update { it.copy(showDeleteDialog = false) }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(deleteErrorMessage = error.message ?: onFailureMessage)
                    }
                }
            _uiState.update { it.copy(isDeletingAccount = false) }
        }
    }
}

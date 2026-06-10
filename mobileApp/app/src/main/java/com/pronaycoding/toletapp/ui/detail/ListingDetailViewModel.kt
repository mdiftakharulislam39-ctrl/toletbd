package com.pronaycoding.toletapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pronaycoding.toletapp.data.model.ToletListing
import com.pronaycoding.toletapp.data.model.UserProfile
import com.pronaycoding.toletapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListingDetailUiState(
    val poster: UserProfile? = null,
    val isSaved: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class ListingDetailViewModel(
    private val userRepository: UserRepository,
    private val listing: ToletListing,
    private val currentUserId: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListingDetailUiState())
    val uiState: StateFlow<ListingDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    fun toggleSaved() {
        viewModelScope.launch {
            if (_uiState.value.isSaved) {
                userRepository.unsaveListing(currentUserId, listing.id)
                    .onSuccess { _uiState.update { it.copy(isSaved = false) } }
            } else {
                userRepository.saveListing(currentUserId, listing.id)
                    .onSuccess { _uiState.update { it.copy(isSaved = true) } }
            }
        }
    }

    private fun loadDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            userRepository.getUserProfile(listing.userId)
                .onSuccess { poster -> _uiState.update { it.copy(poster = poster) } }
                .onFailure { error -> _uiState.update { it.copy(errorMessage = error.message) } }
            val isSaved = userRepository.isListingSaved(currentUserId, listing.id)
            _uiState.update { it.copy(isSaved = isSaved, isLoading = false) }
        }
    }
}

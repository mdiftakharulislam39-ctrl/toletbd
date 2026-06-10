package com.pronaycoding.toletapp.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pronaycoding.toletapp.data.model.ToletListing
import com.pronaycoding.toletapp.domain.repository.ToletRepository
import com.pronaycoding.toletapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SavedUiState(
    val listings: List<ToletListing> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class SavedViewModel(
    private val userRepository: UserRepository,
    private val toletRepository: ToletRepository,
    private val userId: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SavedUiState())
    val uiState: StateFlow<SavedUiState> = _uiState.asStateFlow()

    init {
        loadSavedListings()
    }

    private fun loadSavedListings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            userRepository.getSavedListingIds(userId)
                .onSuccess { ids ->
                    if (ids.isEmpty()) {
                        _uiState.update { it.copy(listings = emptyList()) }
                    } else {
                        toletRepository.getListingsByIds(ids)
                            .onSuccess { listings -> _uiState.update { it.copy(listings = listings) } }
                            .onFailure { error -> _uiState.update { it.copy(errorMessage = error.message) } }
                    }
                }
                .onFailure { error -> _uiState.update { it.copy(errorMessage = error.message) } }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

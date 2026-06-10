package com.pronaycoding.toletapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pronaycoding.toletapp.data.model.ToletListing
import com.pronaycoding.toletapp.domain.repository.ToletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyListingsUiState(
    val listings: List<ToletListing> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val listingToDelete: ToletListing? = null,
    val isDeleting: Boolean = false,
)

class MyListingsViewModel(
    private val toletRepository: ToletRepository,
    private val userId: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyListingsUiState())
    val uiState: StateFlow<MyListingsUiState> = _uiState.asStateFlow()

    init {
        loadListings()
    }

    fun showDeleteDialog(listing: ToletListing) {
        _uiState.update { it.copy(listingToDelete = listing) }
    }

    fun dismissDeleteDialog() {
        if (!_uiState.value.isDeleting) {
            _uiState.update { it.copy(listingToDelete = null) }
        }
    }

    fun confirmDelete() {
        val listing = _uiState.value.listingToDelete ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            toletRepository.deleteListing(listing.id)
                .onSuccess {
                    _uiState.update { it.copy(listingToDelete = null) }
                    loadListings()
                }
                .onFailure { error -> _uiState.update { it.copy(errorMessage = error.message) } }
            _uiState.update { it.copy(isDeleting = false) }
        }
    }

    private fun loadListings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            toletRepository.getListingsByUserId(userId)
                .onSuccess { listings -> _uiState.update { it.copy(listings = listings) } }
                .onFailure { error -> _uiState.update { it.copy(errorMessage = error.message) } }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

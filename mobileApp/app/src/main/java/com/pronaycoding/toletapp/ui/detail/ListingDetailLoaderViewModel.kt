package com.pronaycoding.toletapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pronaycoding.toletapp.data.model.ToletListing
import com.pronaycoding.toletapp.domain.repository.ToletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListingDetailLoaderUiState(
    val listing: ToletListing? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class ListingDetailLoaderViewModel(
    private val toletRepository: ToletRepository,
    private val listingId: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListingDetailLoaderUiState())
    val uiState: StateFlow<ListingDetailLoaderUiState> = _uiState.asStateFlow()

    init {
        loadListing()
    }

    private fun loadListing() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            toletRepository.getListingById(listingId)
                .onSuccess { listing -> _uiState.update { it.copy(listing = listing) } }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: "Listing not found.")
                    }
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

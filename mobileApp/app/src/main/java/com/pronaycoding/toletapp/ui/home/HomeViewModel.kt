package com.pronaycoding.toletapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pronaycoding.toletapp.data.model.ToletListing
import com.pronaycoding.toletapp.domain.repository.ToletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val locationQuery: String = "",
    val listings: List<ToletListing> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class HomeViewModel(
    private val toletRepository: ToletRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadListings("")
    }

    fun onLocationQueryChange(query: String) {
        _uiState.update { it.copy(locationQuery = query) }
    }

    fun search() {
        loadListings(_uiState.value.locationQuery)
    }

    private fun loadListings(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = if (query.isBlank()) {
                toletRepository.getAllListings()
            } else {
                toletRepository.searchByLocation(query)
            }
            result
                .onSuccess { listings -> _uiState.update { it.copy(listings = listings) } }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: "Failed to load listings.")
                    }
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

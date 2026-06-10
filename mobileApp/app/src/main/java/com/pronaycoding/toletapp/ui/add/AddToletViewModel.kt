package com.pronaycoding.toletapp.ui.add

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.pronaycoding.toletapp.data.model.ToletListing
import com.pronaycoding.toletapp.domain.repository.ToletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val propertyTypes = listOf("Flat", "House", "Room", "Office")

data class AddToletUiState(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val price: String = "",
    val bedrooms: String = "",
    val propertyType: String = propertyTypes.first(),
    val propertyTypeExpanded: Boolean = false,
    val existingImages: List<String> = emptyList(),
    val selectedImages: List<Uri> = emptyList(),
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
) {
    val isEditMode: Boolean get() = false // set via ViewModel
    val totalImageCount: Int get() = existingImages.size + selectedImages.size
    val remainingImageSlots: Int get() = ToletRepository.MAX_IMAGES - totalImageCount
}

class AddToletViewModel(
    private val toletRepository: ToletRepository,
    private val user: FirebaseUser,
    private val listingToEdit: ToletListing?,
) : ViewModel() {
    val isEditMode = listingToEdit != null

    private val _uiState = MutableStateFlow(
        AddToletUiState(
            title = listingToEdit?.title.orEmpty(),
            description = listingToEdit?.description.orEmpty(),
            location = listingToEdit?.location.orEmpty(),
            price = listingToEdit?.price.orEmpty(),
            bedrooms = listingToEdit?.bedrooms.orEmpty(),
            propertyType = listingToEdit?.propertyType?.takeIf { it in propertyTypes } ?: propertyTypes.first(),
            existingImages = listingToEdit?.images.orEmpty(),
        ),
    )
    val uiState: StateFlow<AddToletUiState> = _uiState.asStateFlow()

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }
    fun onLocationChange(value: String) = _uiState.update { it.copy(location = value) }
    fun onPriceChange(value: String) = _uiState.update { it.copy(price = value) }
    fun onBedroomsChange(value: String) = _uiState.update { it.copy(bedrooms = value) }
    fun onPropertyTypeChange(value: String) = _uiState.update { it.copy(propertyType = value) }
    fun onPropertyTypeExpandedChange(expanded: Boolean) =
        _uiState.update { it.copy(propertyTypeExpanded = expanded) }

    fun addSelectedImages(uris: List<Uri>) {
        val slots = _uiState.value.remainingImageSlots.coerceAtLeast(0)
        _uiState.update {
            it.copy(selectedImages = (it.selectedImages + uris).take(slots))
        }
    }

    fun removeExistingImage(index: Int) {
        _uiState.update {
            it.copy(existingImages = it.existingImages.filterIndexed { i, _ -> i != index })
        }
    }

    fun removeSelectedImage(index: Int) {
        _uiState.update {
            it.copy(selectedImages = it.selectedImages.filterIndexed { i, _ -> i != index })
        }
    }

    fun submit(context: Context, onSuccess: () -> Unit) {
        val state = _uiState.value
        when {
            state.title.isBlank() -> _uiState.update { it.copy(errorMessage = "Title is required.") }
            state.location.isBlank() -> _uiState.update { it.copy(errorMessage = "Location is required.") }
            state.price.isBlank() -> _uiState.update { it.copy(errorMessage = "Price is required.") }
            else -> viewModelScope.launch {
                _uiState.update { it.copy(isSubmitting = true, errorMessage = null, successMessage = null) }

                if (isEditMode) {
                    val updatedListing = listingToEdit!!.copy(
                        title = state.title.trim(),
                        description = state.description.trim(),
                        location = state.location.trim(),
                        locationLower = state.location.trim().lowercase(),
                        price = state.price.trim(),
                        bedrooms = state.bedrooms.trim().ifBlank { "N/A" },
                        propertyType = state.propertyType,
                        images = state.existingImages,
                    )
                    toletRepository.updateListing(context, updatedListing, state.selectedImages)
                        .onSuccess {
                            _uiState.update { it.copy(successMessage = "Listing updated successfully!") }
                            onSuccess()
                        }
                        .onFailure { error ->
                            _uiState.update {
                                it.copy(errorMessage = error.message ?: "Failed to update listing.")
                            }
                        }
                } else {
                    val listing = ToletListing(
                        userId = user.uid,
                        userName = user.displayName ?: "User",
                        title = state.title.trim(),
                        description = state.description.trim(),
                        location = state.location.trim(),
                        locationLower = state.location.trim().lowercase(),
                        price = state.price.trim(),
                        bedrooms = state.bedrooms.trim().ifBlank { "N/A" },
                        propertyType = state.propertyType,
                        createdAt = System.currentTimeMillis(),
                    )
                    toletRepository.createListing(context, listing, state.selectedImages)
                        .onSuccess {
                            _uiState.update {
                                it.copy(
                                    successMessage = "To-let posted successfully!",
                                    title = "",
                                    description = "",
                                    location = "",
                                    price = "",
                                    bedrooms = "",
                                    selectedImages = emptyList(),
                                )
                            }
                            onSuccess()
                        }
                        .onFailure { error ->
                            _uiState.update {
                                it.copy(errorMessage = error.message ?: "Failed to post listing.")
                            }
                        }
                }

                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }
}

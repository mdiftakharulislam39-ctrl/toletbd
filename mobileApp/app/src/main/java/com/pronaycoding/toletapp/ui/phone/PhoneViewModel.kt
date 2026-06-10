package com.pronaycoding.toletapp.ui.phone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.pronaycoding.toletapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PhoneUiState(
    val phoneNumber: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

class PhoneViewModel(
    private val userRepository: UserRepository,
    private val user: FirebaseUser,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PhoneUiState())
    val uiState: StateFlow<PhoneUiState> = _uiState.asStateFlow()

    fun onPhoneNumberChange(phone: String) {
        _uiState.update { it.copy(phoneNumber = phone, errorMessage = null) }
    }

    fun savePhone(invalidPhoneMessage: String, saveFailedMessage: String, onSuccess: () -> Unit) {
        val phone = _uiState.value.phoneNumber
        if (!UserRepository.isValidPhone(phone)) {
            _uiState.update { it.copy(errorMessage = invalidPhoneMessage) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            userRepository.savePhoneNumber(user, phone)
                .onSuccess { onSuccess() }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: saveFailedMessage)
                    }
                }
            _uiState.update { it.copy(isSaving = false) }
        }
    }
}

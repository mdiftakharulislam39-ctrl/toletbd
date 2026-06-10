package com.pronaycoding.toletapp.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pronaycoding.toletapp.domain.repository.AccountRepository
import com.pronaycoding.toletapp.domain.repository.AuthRepository
import com.pronaycoding.toletapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionUiState(
    val currentUser: FirebaseUser? = null,
    val hasPhone: Boolean? = null,
    val isSigningIn: Boolean = false,
    val signInError: String? = null,
)

class SessionViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val accountRepository: AccountRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SessionUiState(currentUser = authRepository.currentUser))
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        _uiState.update { it.copy(currentUser = user, hasPhone = if (user == null) null else it.hasPhone) }
        if (user != null) {
            checkPhoneNumber(user.uid)
        }
    }

    init {
        authRepository.addAuthStateListener(authStateListener)
        authRepository.currentUser?.let { checkPhoneNumber(it.uid) }
    }

    fun signIn() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSigningIn = true, signInError = null) }
            authRepository.signInWithGoogle()
                .onSuccess { _uiState.update { it.copy(signInError = null) } }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(signInError = error.message ?: "Sign in failed.")
                    }
                }
            _uiState.update { it.copy(isSigningIn = false) }
        }
    }

    fun onPhoneSaved() {
        _uiState.update { it.copy(hasPhone = true) }
    }

    fun signOut() {
        authRepository.signOut()
    }

    suspend fun deleteAccount(): Result<Unit> {
        val user = authRepository.currentUser
            ?: return Result.failure(IllegalStateException("Not signed in."))
        return authRepository.reauthenticateWithGoogle(user).fold(
            onSuccess = { accountRepository.deleteAccount(user) },
            onFailure = { Result.failure(it) },
        )
    }

    private fun checkPhoneNumber(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(hasPhone = null) }
            val hasPhone = userRepository.hasPhoneNumber(userId)
            _uiState.update { it.copy(hasPhone = hasPhone) }
        }
    }

    override fun onCleared() {
        authRepository.removeAuthStateListener(authStateListener)
        super.onCleared()
    }
}

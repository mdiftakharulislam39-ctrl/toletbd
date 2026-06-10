package com.pronaycoding.toletapp.domain.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?

    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener)

    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener)

    suspend fun signInWithGoogle(): Result<Unit>

    suspend fun reauthenticateWithGoogle(user: FirebaseUser): Result<Unit>

    fun signOut()
}

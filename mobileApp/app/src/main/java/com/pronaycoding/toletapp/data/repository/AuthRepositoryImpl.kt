package com.pronaycoding.toletapp.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pronaycoding.toletapp.auth.GoogleAuthManager
import com.pronaycoding.toletapp.domain.repository.AuthRepository

class AuthRepositoryImpl(
    context: Context,
) : AuthRepository {
    private val authManager = GoogleAuthManager(context)
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        firebaseAuth.addAuthStateListener(listener)
    }

    override fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        firebaseAuth.removeAuthStateListener(listener)
    }

    override suspend fun signInWithGoogle(): Result<Unit> = authManager.signInWithGoogle()

    override suspend fun reauthenticateWithGoogle(user: FirebaseUser): Result<Unit> =
        authManager.reauthenticateWithGoogle(user)

    override fun signOut() = authManager.signOut()
}

package com.pronaycoding.toletapp.domain.repository

import com.google.firebase.auth.FirebaseUser

interface AccountRepository {
    suspend fun deleteAccount(user: FirebaseUser): Result<Unit>
}

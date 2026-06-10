package com.pronaycoding.toletapp.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.pronaycoding.toletapp.R
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(
    private val context: Context,
    private val credentialManager: CredentialManager = CredentialManager.create(context),
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
) {
    suspend fun signInWithGoogle(): Result<Unit> {
        val webClientId = resolveWebClientId()
        if (webClientId.isBlank() || webClientId == PLACEHOLDER_WEB_CLIENT_ID) {
            return Result.failure(
                IllegalStateException(
                    "Google Sign-In is not configured. Enable Google auth in Firebase Console, " +
                        "add your app SHA-1 fingerprint, then re-download google-services.json " +
                        "or set google_web_client_id in strings.xml.",
                ),
            )
        }

        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )

            val credential = result.credential
            if (credential !is CustomCredential ||
                credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                return Result.failure(IllegalStateException("Unexpected credential type."))
            }

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val firebaseCredential = GoogleAuthProvider.getCredential(
                googleIdTokenCredential.idToken,
                null,
            )

            firebaseAuth.signInWithCredential(firebaseCredential).await()
            Result.success(Unit)
        } catch (e: GetCredentialException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    private fun resolveWebClientId(): String {
        val generatedId = context.resources.getIdentifier(
            "default_web_client_id",
            "string",
            context.packageName,
        )
        if (generatedId != 0) {
            val value = context.getString(generatedId)
            if (value.isNotBlank()) {
                return value
            }
        }
        return context.getString(R.string.google_web_client_id)
    }

    companion object {
        private const val PLACEHOLDER_WEB_CLIENT_ID = "YOUR_WEB_CLIENT_ID"
    }
}

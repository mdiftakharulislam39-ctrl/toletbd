package com.pronaycoding.toletapp.ui.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseUser
import com.pronaycoding.toletapp.R
import com.pronaycoding.toletapp.data.ToletRepository
import com.pronaycoding.toletapp.data.UserRepository
import com.pronaycoding.toletapp.data.model.ToletListing
import com.pronaycoding.toletapp.ui.home.ToletListingCard

@Composable
fun SavedScreen(
    user: FirebaseUser,
    onListingClick: (ToletListing) -> Unit,
    modifier: Modifier = Modifier,
    userRepository: UserRepository = remember { UserRepository() },
    toletRepository: ToletRepository = remember { ToletRepository() },
) {
    var listings by remember { mutableStateOf<List<ToletListing>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(user.uid) {
        isLoading = true
        val idsResult = userRepository.getSavedListingIds(user.uid)
        idsResult
            .onSuccess { ids ->
                if (ids.isEmpty()) {
                    listings = emptyList()
                } else {
                    toletRepository.getListingsByIds(ids)
                        .onSuccess { listings = it }
                        .onFailure { errorMessage = it.message }
                }
            }
            .onFailure { errorMessage = it.message }
        isLoading = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.saved_title),
            style = MaterialTheme.typography.headlineSmall,
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }

            listings.isEmpty() -> {
                Text(
                    text = stringResource(R.string.no_saved_listings),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 24.dp),
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                ) {
                    items(listings, key = { it.id }) { listing ->
                        ToletListingCard(
                            listing = listing,
                            onClick = { onListingClick(listing) },
                        )
                    }
                }
            }
        }
    }
}

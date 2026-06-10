package com.pronaycoding.toletapp.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseUser
import com.pronaycoding.toletapp.R
import com.pronaycoding.toletapp.data.ToletRepository
import com.pronaycoding.toletapp.data.model.ToletListing
import com.pronaycoding.toletapp.ui.home.ToletListingCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListingsScreen(
    user: FirebaseUser,
    onBack: () -> Unit,
    onEditListing: (ToletListing) -> Unit,
    modifier: Modifier = Modifier,
    toletRepository: ToletRepository = remember { ToletRepository() },
) {
    var listings by remember { mutableStateOf<List<ToletListing>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var listingToDelete by remember { mutableStateOf<ToletListing?>(null) }
    var isDeleting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun loadListings() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            toletRepository.getListingsByUserId(user.uid)
                .onSuccess { listings = it }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    LaunchedEffect(user.uid) {
        loadListings()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_listings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
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
                        text = stringResource(R.string.no_my_listings),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 24.dp),
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp),
                    ) {
                        items(listings, key = { it.id }) { listing ->
                            Column {
                                ToletListingCard(listing = listing)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    OutlinedButton(
                                        onClick = { onEditListing(listing) },
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Text(stringResource(R.string.edit_listing))
                                    }
                                    OutlinedButton(
                                        onClick = { listingToDelete = listing },
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Text(
                                            text = stringResource(R.string.delete_listing),
                                            color = MaterialTheme.colorScheme.error,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    listingToDelete?.let { listing ->
        AlertDialog(
            onDismissRequest = { if (!isDeleting) listingToDelete = null },
            title = { Text(stringResource(R.string.delete_listing)) },
            text = { Text(stringResource(R.string.delete_listing_confirm, listing.title)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (isDeleting) return@TextButton
                        coroutineScope.launch {
                            isDeleting = true
                            toletRepository.deleteListing(listing.id)
                                .onSuccess {
                                    listingToDelete = null
                                    loadListings()
                                }
                                .onFailure { errorMessage = it.message }
                            isDeleting = false
                        }
                    },
                    enabled = !isDeleting,
                ) {
                    Text(
                        text = stringResource(R.string.delete_listing),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { listingToDelete = null },
                    enabled = !isDeleting,
                ) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }
}

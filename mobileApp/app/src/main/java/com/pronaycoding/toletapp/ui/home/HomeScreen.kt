package com.pronaycoding.toletapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.pronaycoding.toletapp.R
import com.pronaycoding.toletapp.data.ToletRepository
import com.pronaycoding.toletapp.data.model.ToletListing
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    repository: ToletRepository = remember { ToletRepository() },
) {
    var locationQuery by remember { mutableStateOf("") }
    var listings by remember { mutableStateOf<List<ToletListing>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    fun loadListings(query: String = locationQuery) {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            val result = if (query.isBlank()) {
                repository.getAllListings()
            } else {
                repository.searchByLocation(query)
            }
            result
                .onSuccess { listings = it }
                .onFailure { errorMessage = it.message ?: "Failed to load listings." }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadListings("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.search_title),
            style = MaterialTheme.typography.headlineSmall,
        )

        OutlinedTextField(
            value = locationQuery,
            onValueChange = { locationQuery = it },
            label = { Text(stringResource(R.string.location_hint)) },
            placeholder = { Text(stringResource(R.string.location_placeholder)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            singleLine = true,
        )

        Button(
            onClick = { loadListings(locationQuery) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Text(stringResource(R.string.search_button))
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
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
                    text = stringResource(R.string.no_listings),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 24.dp),
                )
            }

            else -> {
                Text(
                    text = stringResource(R.string.results_count, listings.size),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                ) {
                    items(listings, key = { it.id }) { listing ->
                        ToletListingCard(listing = listing)
                    }
                }
            }
        }
    }
}

package com.pronaycoding.toletapp.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseUser
import com.pronaycoding.toletapp.R
import com.pronaycoding.toletapp.data.ToletRepository
import com.pronaycoding.toletapp.data.UserRepository
import com.pronaycoding.toletapp.data.model.ToletListing
import com.pronaycoding.toletapp.data.model.UserProfile
import com.pronaycoding.toletapp.ui.common.ListingImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToletDetailScreen(
    listing: ToletListing,
    currentUser: FirebaseUser,
    onBack: () -> Unit,
    onChatClick: (UserProfile) -> Unit,
    modifier: Modifier = Modifier,
    userRepository: UserRepository = remember { UserRepository() },
) {
    var poster by remember { mutableStateOf<UserProfile?>(null) }
    var isSaved by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(listing.userId, listing.id) {
        isLoading = true
        userRepository.getUserProfile(listing.userId)
            .onSuccess { poster = it }
            .onFailure { errorMessage = it.message }
        isSaved = userRepository.isListingSaved(currentUser.uid, listing.id)
        isLoading = false
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(listing.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                if (isSaved) {
                                    userRepository.unsaveListing(currentUser.uid, listing.id)
                                        .onSuccess { isSaved = false }
                                } else {
                                    userRepository.saveListing(currentUser.uid, listing.id)
                                        .onSuccess { isSaved = true }
                                }
                            }
                        },
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(R.string.save_listing),
                            tint = if (isSaved) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            listing.images.forEach { image ->
                ListingImage(
                    imageData = image,
                    contentDescription = listing.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .padding(bottom = 8.dp),
                    contentScale = ContentScale.Crop,
                )
            }

            Text(
                text = listing.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "৳${listing.price}/month",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp),
            )

            DetailRow(label = stringResource(R.string.location_hint), value = listing.location)
            DetailRow(label = stringResource(R.string.property_type_hint), value = listing.propertyType)
            DetailRow(label = stringResource(R.string.bedrooms_hint), value = listing.bedrooms)

            if (listing.description.isNotBlank()) {
                Text(
                    text = stringResource(R.string.description_hint),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 16.dp),
                )
                Text(
                    text = listing.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.posted_by),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(12.dp))

            poster?.let { profile ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (profile.photoUrl.isNotBlank()) {
                        AsyncImage(
                            model = profile.photoUrl,
                            contentDescription = profile.displayName,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    }

                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(
                            text = profile.displayName.ifBlank { listing.userName },
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = profile.phoneNumber,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                if (profile.userId != currentUser.uid) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onChatClick(profile) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.chat_with_poster))
                    }
                }
            } ?: run {
                Text(
                    text = listing.userName,
                    style = MaterialTheme.typography.bodyLarge,
                )
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.Medium)
    }
}

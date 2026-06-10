package com.pronaycoding.toletapp.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.pronaycoding.toletapp.data.UserRepository
import com.pronaycoding.toletapp.data.model.ToletListing
import com.pronaycoding.toletapp.data.model.UserProfile
import com.pronaycoding.toletapp.ui.add.AddToletScreen

private enum class ProfileRoute {
    Main,
    MyListings,
    EditListing,
}

@Composable
fun ProfileScreen(
    user: FirebaseUser,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier,
    userRepository: UserRepository = remember { UserRepository() },
) {
    var route by remember { mutableStateOf(ProfileRoute.Main) }
    var editingListing by remember { mutableStateOf<ToletListing?>(null) }

    when (route) {
        ProfileRoute.Main -> ProfileMainScreen(
            user = user,
            userRepository = userRepository,
            onViewMyListings = { route = ProfileRoute.MyListings },
            onSignOutClick = onSignOutClick,
            modifier = modifier,
        )

        ProfileRoute.MyListings -> MyListingsScreen(
            user = user,
            onBack = { route = ProfileRoute.Main },
            onEditListing = { listing ->
                editingListing = listing
                route = ProfileRoute.EditListing
            },
            modifier = modifier,
        )

        ProfileRoute.EditListing -> {
            val listing = editingListing
            if (listing != null) {
                AddToletScreen(
                    user = user,
                    listingToEdit = listing,
                    onListingPosted = {
                        editingListing = null
                        route = ProfileRoute.MyListings
                    },
                    onBack = {
                        editingListing = null
                        route = ProfileRoute.MyListings
                    },
                    modifier = modifier,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileMainScreen(
    user: FirebaseUser,
    userRepository: UserRepository,
    onViewMyListings: () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var profile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(user.uid) {
        isLoading = true
        userRepository.getUserProfile(user.uid)
            .onSuccess { profile = it }
            .onFailure { errorMessage = it.message }
        isLoading = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.profile_title),
            style = MaterialTheme.typography.headlineSmall,
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                ProfileDetailsCard(
                    user = user,
                    profile = profile,
                    modifier = Modifier.padding(top = 16.dp),
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }

                Card(
                    onClick = onViewMyListings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.view_my_listings),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = onSignOutClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.sign_out))
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailsCard(
    user: FirebaseUser,
    profile: UserProfile?,
    modifier: Modifier = Modifier,
) {
    val displayName = profile?.displayName?.takeIf { it.isNotBlank() }
        ?: user.displayName.orEmpty()
    val photoUrl = profile?.photoUrl?.takeIf { it.isNotBlank() }
        ?: user.photoUrl?.toString().orEmpty()
    val phone = profile?.phoneNumber.orEmpty()
    val email = user.email.orEmpty()

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (photoUrl.isNotBlank()) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = displayName,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (displayName.isNotBlank()) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            ProfileDetailRow(
                label = stringResource(R.string.profile_email),
                value = email,
            )
            ProfileDetailRow(
                label = stringResource(R.string.profile_phone),
                value = phone.ifBlank { "—" },
            )
            ProfileDetailRow(
                label = stringResource(R.string.profile_user_id),
                value = user.uid,
            )
        }
    }
}

@Composable
private fun ProfileDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    if (value.isBlank()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

package com.pronaycoding.toletapp.ui.profile

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.pronaycoding.toletapp.data.model.ToletListing
import com.pronaycoding.toletapp.data.model.UserProfile
import com.pronaycoding.toletapp.ui.add.AddToletScreen
import org.koin.compose.viewmodel.koinViewModel

private enum class ProfileRoute {
    Main,
    MyListings,
    EditListing,
}

@Composable
fun ProfileScreen(
    user: FirebaseUser,
    onSignOutClick: () -> Unit,
    onDeleteAccount: suspend () -> Result<Unit>,
    onSubRouteChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    var route by remember { mutableStateOf(ProfileRoute.Main) }
    var editingListing by remember { mutableStateOf<ToletListing?>(null) }

    LaunchedEffect(route) {
        onSubRouteChange(route != ProfileRoute.Main)
    }

    BackHandler(enabled = route != ProfileRoute.Main) {
        when (route) {
            ProfileRoute.EditListing -> {
                editingListing = null
                route = ProfileRoute.MyListings
            }
            ProfileRoute.MyListings -> route = ProfileRoute.Main
            ProfileRoute.Main -> Unit
        }
    }

    when (route) {
        ProfileRoute.Main -> ProfileMainScreen(
            user = user,
            viewModel = viewModel,
            onViewMyListings = { route = ProfileRoute.MyListings },
            onSignOutClick = onSignOutClick,
            onDeleteAccount = onDeleteAccount,
            onAccountDeleted = onSignOutClick,
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
    viewModel: ProfileViewModel,
    onViewMyListings: () -> Unit,
    onSignOutClick: () -> Unit,
    onDeleteAccount: suspend () -> Result<Unit>,
    onAccountDeleted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val deleteFailedMessage = stringResource(R.string.delete_account_failed)

    LaunchedEffect(user.uid) {
        viewModel.loadProfile(user.uid)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.profile_title),
            style = MaterialTheme.typography.headlineSmall,
        )

        when {
            uiState.isLoading -> {
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
                    profile = uiState.profile,
                    modifier = Modifier.padding(top = 16.dp),
                )

                uiState.errorMessage?.let { message ->
                    Text(
                        text = message,
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
                    enabled = !uiState.isDeletingAccount,
                ) {
                    Text(text = stringResource(R.string.sign_out))
                }

                TextButton(
                    onClick = viewModel::showDeleteDialog,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    enabled = !uiState.isDeletingAccount,
                ) {
                    Text(
                        text = stringResource(R.string.delete_account),
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                if (uiState.isDeletingAccount) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.deleteErrorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
    }

    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteDialog,
            title = { Text(stringResource(R.string.delete_account_confirm_title)) },
            text = { Text(stringResource(R.string.delete_account_confirm_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAccount(
                            onDelete = onDeleteAccount,
                            onSuccess = onAccountDeleted,
                            onFailureMessage = deleteFailedMessage,
                        )
                    },
                    enabled = !uiState.isDeletingAccount,
                ) {
                    Text(
                        text = stringResource(R.string.delete_account),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = viewModel::dismissDeleteDialog,
                    enabled = !uiState.isDeletingAccount,
                ) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
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

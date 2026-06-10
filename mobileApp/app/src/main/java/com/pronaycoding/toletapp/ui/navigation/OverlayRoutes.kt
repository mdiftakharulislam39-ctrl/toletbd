package com.pronaycoding.toletapp.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.google.firebase.auth.FirebaseUser
import com.pronaycoding.toletapp.data.ToletRepository
import com.pronaycoding.toletapp.data.UserRepository
import com.pronaycoding.toletapp.data.model.ToletListing
import com.pronaycoding.toletapp.data.model.UserProfile
import com.pronaycoding.toletapp.ui.chat.ChatScreen
import com.pronaycoding.toletapp.ui.detail.ToletDetailScreen

@Composable
fun OverlayHost(
    overlay: OverlayDestination,
    currentUser: FirebaseUser,
    navigator: AppNavigator,
    modifier: Modifier = Modifier,
    toletRepository: ToletRepository = remember { ToletRepository() },
    userRepository: UserRepository = remember { UserRepository() },
) {
    BackHandler(onBack = navigator::popOverlay)

    when (overlay) {
        is OverlayDestination.ListingDetail -> ListingDetailRoute(
            listingId = overlay.listingId,
            currentUser = currentUser,
            toletRepository = toletRepository,
            userRepository = userRepository,
            onBack = navigator::popOverlay,
            onChatClick = navigator::openChat,
            modifier = modifier,
        )

        is OverlayDestination.Chat -> ChatRoute(
            otherUserId = overlay.otherUserId,
            currentUser = currentUser,
            userRepository = userRepository,
            onBack = navigator::popOverlay,
            modifier = modifier,
        )
    }
}

@Composable
private fun ListingDetailRoute(
    listingId: String,
    currentUser: FirebaseUser,
    onBack: () -> Unit,
    onChatClick: (UserProfile) -> Unit,
    modifier: Modifier = Modifier,
    toletRepository: ToletRepository,
    userRepository: UserRepository,
) {
    var listing by remember(listingId) { mutableStateOf<ToletListing?>(null) }
    var errorMessage by remember(listingId) { mutableStateOf<String?>(null) }

    LaunchedEffect(listingId) {
        toletRepository.getListingById(listingId)
            .onSuccess { listing = it }
            .onFailure { errorMessage = it.message ?: "Listing not found." }
    }

    when {
        listing != null -> {
            ToletDetailScreen(
                listing = listing!!,
                currentUser = currentUser,
                userRepository = userRepository,
                onBack = onBack,
                onChatClick = onChatClick,
                modifier = modifier,
            )
        }

        errorMessage != null -> RouteMessageScreen(
            message = errorMessage!!,
            modifier = modifier,
        )

        else -> RouteLoadingScreen(modifier = modifier)
    }
}

@Composable
private fun ChatRoute(
    otherUserId: String,
    currentUser: FirebaseUser,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    userRepository: UserRepository,
) {
    var otherUser by remember(otherUserId) { mutableStateOf<UserProfile?>(null) }
    var errorMessage by remember(otherUserId) { mutableStateOf<String?>(null) }

    LaunchedEffect(otherUserId) {
        userRepository.getUserProfile(otherUserId)
            .onSuccess { otherUser = it }
            .onFailure {
                otherUser = UserProfile(userId = otherUserId, displayName = "User")
            }
    }

    when {
        otherUser != null -> {
            ChatScreen(
                currentUser = currentUser,
                otherUser = otherUser!!,
                onBack = onBack,
                modifier = modifier,
            )
        }

        errorMessage != null -> RouteMessageScreen(
            message = errorMessage!!,
            modifier = modifier,
        )

        else -> RouteLoadingScreen(modifier = modifier)
    }
}

@Composable
private fun RouteLoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun RouteMessageScreen(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

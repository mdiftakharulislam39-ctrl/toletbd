package com.pronaycoding.toletapp.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseUser
import com.pronaycoding.toletapp.data.model.UserProfile
import com.pronaycoding.toletapp.ui.chat.ChatRouteViewModel
import com.pronaycoding.toletapp.ui.chat.ChatScreen
import com.pronaycoding.toletapp.ui.detail.ListingDetailLoaderViewModel
import com.pronaycoding.toletapp.ui.detail.ToletDetailScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun OverlayHost(
    overlay: OverlayDestination,
    currentUser: FirebaseUser,
    navigator: AppNavigator,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = navigator::popOverlay)

    when (overlay) {
        is OverlayDestination.ListingDetail -> ListingDetailRoute(
            listingId = overlay.listingId,
            currentUser = currentUser,
            onBack = navigator::popOverlay,
            onChatClick = navigator::openChat,
            modifier = modifier,
        )

        is OverlayDestination.Chat -> ChatRoute(
            otherUserId = overlay.otherUserId,
            currentUser = currentUser,
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
    loaderViewModel: ListingDetailLoaderViewModel = koinViewModel { parametersOf(listingId) },
) {
    val loaderState by loaderViewModel.uiState.collectAsState()

    when {
        loaderState.listing != null -> {
            ToletDetailScreen(
                listing = loaderState.listing!!,
                currentUser = currentUser,
                onBack = onBack,
                onChatClick = onChatClick,
                modifier = modifier,
            )
        }

        loaderState.errorMessage != null -> RouteMessageScreen(
            message = loaderState.errorMessage!!,
            modifier = modifier,
        )

        loaderState.isLoading -> RouteLoadingScreen(modifier = modifier)
    }
}

@Composable
private fun ChatRoute(
    otherUserId: String,
    currentUser: FirebaseUser,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    routeViewModel: ChatRouteViewModel = koinViewModel { parametersOf(otherUserId) },
) {
    val routeState by routeViewModel.uiState.collectAsState()

    when {
        routeState.otherUser != null -> {
            ChatScreen(
                currentUser = currentUser,
                otherUser = routeState.otherUser!!,
                onBack = onBack,
                modifier = modifier,
            )
        }

        routeState.errorMessage != null -> RouteMessageScreen(
            message = routeState.errorMessage!!,
            modifier = modifier,
        )

        routeState.isLoading -> RouteLoadingScreen(modifier = modifier)
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

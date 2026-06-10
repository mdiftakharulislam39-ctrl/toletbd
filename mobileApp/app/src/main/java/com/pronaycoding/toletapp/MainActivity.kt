package com.pronaycoding.toletapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.google.firebase.auth.FirebaseUser
import com.pronaycoding.toletapp.data.model.ToletListing
import com.pronaycoding.toletapp.data.model.UserProfile
import com.pronaycoding.toletapp.ui.add.AddToletScreen
import com.pronaycoding.toletapp.ui.auth.SignInScreen
import com.pronaycoding.toletapp.ui.chat.ChatListScreen
import com.pronaycoding.toletapp.ui.home.HomeScreen
import com.pronaycoding.toletapp.ui.navigation.AppDestinations
import com.pronaycoding.toletapp.ui.navigation.OverlayHost
import com.pronaycoding.toletapp.ui.navigation.ToletBottomBar
import com.pronaycoding.toletapp.ui.navigation.rememberAppNavigator
import com.pronaycoding.toletapp.ui.phone.PhoneNumberScreen
import com.pronaycoding.toletapp.ui.profile.ProfileScreen
import com.pronaycoding.toletapp.ui.saved.SavedScreen
import com.pronaycoding.toletapp.ui.session.SessionViewModel
import com.pronaycoding.toletapp.ui.theme.ToletAppTheme
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToletAppTheme {
                ToletAppApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun ToletAppApp(
    sessionViewModel: SessionViewModel = koinViewModel(),
) {
    val sessionState by sessionViewModel.uiState.collectAsState()
    val navigator = rememberAppNavigator()
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var profileHasSubRoute by remember { mutableStateOf(false) }

    LaunchedEffect(sessionState.currentUser) {
        if (sessionState.currentUser == null) {
            navigator.clearOverlays()
        }
    }

    val user = sessionState.currentUser
    if (user == null) {
        SignInScreen(
            isLoading = sessionState.isSigningIn,
            errorMessage = sessionState.signInError,
            onSignInClick = sessionViewModel::signIn,
        )
        return
    }

    when {
        sessionState.hasPhone == null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        sessionState.hasPhone == false -> {
            PhoneNumberScreen(
                user = user,
                onPhoneSaved = sessionViewModel::onPhoneSaved,
            )
        }

        navigator.currentOverlay != null -> {
            OverlayHost(
                overlay = navigator.currentOverlay!!,
                currentUser = user,
                navigator = navigator,
                modifier = Modifier.fillMaxSize(),
            )
        }

        else -> {
            MainNavigation(
                user = user,
                currentDestination = currentDestination,
                onDestinationChange = { currentDestination = it },
                onListingClick = navigator::openListing,
                onChatClick = navigator::openChat,
                profileHasSubRoute = profileHasSubRoute,
                onProfileSubRouteChange = { profileHasSubRoute = it },
                onSignOut = sessionViewModel::signOut,
                onDeleteAccount = sessionViewModel::deleteAccount,
            )
        }
    }
}

@Composable
private fun MainNavigation(
    user: FirebaseUser,
    currentDestination: AppDestinations,
    onDestinationChange: (AppDestinations) -> Unit,
    onListingClick: (ToletListing) -> Unit,
    onChatClick: (UserProfile) -> Unit,
    profileHasSubRoute: Boolean,
    onProfileSubRouteChange: (Boolean) -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: suspend () -> Result<Unit>,
) {
    val shouldHandleTabBack = currentDestination != AppDestinations.HOME &&
        !(currentDestination == AppDestinations.PROFILE && profileHasSubRoute)

    BackHandler(enabled = shouldHandleTabBack) {
        onDestinationChange(AppDestinations.HOME)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                ToletBottomBar(
                    currentDestination = currentDestination,
                    onDestinationChange = onDestinationChange,
                )
            },
        ) { innerPadding ->
            val tabModifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())

            when (currentDestination) {
                AppDestinations.HOME -> HomeScreen(
                    onListingClick = onListingClick,
                    modifier = tabModifier,
                )

                AppDestinations.POST -> AddToletScreen(
                    user = user,
                    modifier = tabModifier,
                    onListingPosted = { onDestinationChange(AppDestinations.HOME) },
                )

                AppDestinations.SAVED -> SavedScreen(
                    user = user,
                    onListingClick = onListingClick,
                    modifier = tabModifier,
                )

                AppDestinations.CHAT -> ChatListScreen(
                    user = user,
                    onChatClick = onChatClick,
                    modifier = tabModifier,
                )

                AppDestinations.PROFILE -> ProfileScreen(
                    user = user,
                    onSignOutClick = onSignOut,
                    onDeleteAccount = onDeleteAccount,
                    onSubRouteChange = onProfileSubRouteChange,
                    modifier = tabModifier,
                )
            }
        }
    }
}

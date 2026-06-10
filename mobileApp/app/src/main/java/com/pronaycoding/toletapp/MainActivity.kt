package com.pronaycoding.toletapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pronaycoding.toletapp.auth.GoogleAuthManager
import com.pronaycoding.toletapp.data.model.ToletListing
import com.pronaycoding.toletapp.data.model.UserProfile
import com.pronaycoding.toletapp.data.UserRepository
import com.pronaycoding.toletapp.ui.add.AddToletScreen
import com.pronaycoding.toletapp.ui.auth.SignInScreen
import com.pronaycoding.toletapp.ui.chat.ChatListScreen
import com.pronaycoding.toletapp.ui.chat.ChatScreen
import com.pronaycoding.toletapp.ui.detail.ToletDetailScreen
import com.pronaycoding.toletapp.ui.home.HomeScreen
import com.pronaycoding.toletapp.ui.navigation.AppDestinations
import com.pronaycoding.toletapp.ui.navigation.ToletBottomBar
import com.pronaycoding.toletapp.ui.phone.PhoneNumberScreen
import com.pronaycoding.toletapp.ui.profile.ProfileScreen
import com.pronaycoding.toletapp.ui.saved.SavedScreen
import com.pronaycoding.toletapp.ui.theme.ToletAppTheme
import kotlinx.coroutines.launch

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
fun ToletAppApp() {
    val context = LocalContext.current
    val authManager = remember { GoogleAuthManager(context) }
    val userRepository = remember { UserRepository() }
    val coroutineScope = rememberCoroutineScope()

    var currentUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var isSigningIn by rememberSaveable { mutableStateOf(false) }
    var signInError by rememberSaveable { mutableStateOf<String?>(null) }
    var hasPhone by remember { mutableStateOf<Boolean?>(null) }
    var selectedListing by remember { mutableStateOf<ToletListing?>(null) }
    var chatWithUser by remember { mutableStateOf<UserProfile?>(null) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        FirebaseAuth.getInstance().addAuthStateListener(listener)
        onDispose {
            FirebaseAuth.getInstance().removeAuthStateListener(listener)
        }
    }

    val user = currentUser
    if (user == null) {
        SignInScreen(
            isLoading = isSigningIn,
            errorMessage = signInError,
            onSignInClick = {
                coroutineScope.launch {
                    isSigningIn = true
                    signInError = null
                    authManager.signInWithGoogle()
                        .onSuccess { signInError = null }
                        .onFailure { error ->
                            signInError = error.message ?: "Sign in failed."
                        }
                    isSigningIn = false
                }
            },
        )
        return
    }

    LaunchedEffect(user.uid) {
        hasPhone = userRepository.hasPhoneNumber(user.uid)
    }

    when {
        hasPhone == null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        hasPhone == false -> {
            PhoneNumberScreen(
                user = user,
                userRepository = userRepository,
                onPhoneSaved = { hasPhone = true },
            )
        }

        chatWithUser != null -> {
            ChatScreen(
                currentUser = user,
                otherUser = chatWithUser!!,
                onBack = { chatWithUser = null },
                modifier = Modifier.fillMaxSize(),
            )
        }

        selectedListing != null -> {
            ToletDetailScreen(
                listing = selectedListing!!,
                currentUser = user,
                userRepository = userRepository,
                onBack = { selectedListing = null },
                onChatClick = { profile ->
                    chatWithUser = profile
                },
                modifier = Modifier.fillMaxSize(),
            )
        }

        else -> {
            MainNavigation(
                user = user,
                currentDestination = currentDestination,
                onDestinationChange = { currentDestination = it },
                onListingClick = { selectedListing = it },
                onChatClick = { chatWithUser = it },
                onSignOut = { authManager.signOut() },
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
    onSignOut: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            ToletBottomBar(
                currentDestination = currentDestination,
                onDestinationChange = onDestinationChange,
            )
        },
    ) { innerPadding ->
        when (currentDestination) {
            AppDestinations.HOME -> HomeScreen(
                onListingClick = onListingClick,
                modifier = Modifier.padding(innerPadding),
            )

            AppDestinations.POST -> AddToletScreen(
                user = user,
                modifier = Modifier.padding(innerPadding),
                onListingPosted = { onDestinationChange(AppDestinations.HOME) },
            )

            AppDestinations.SAVED -> SavedScreen(
                user = user,
                onListingClick = onListingClick,
                modifier = Modifier.padding(innerPadding),
            )

            AppDestinations.CHAT -> ChatListScreen(
                user = user,
                onChatClick = onChatClick,
                modifier = Modifier.padding(innerPadding),
            )

            AppDestinations.PROFILE -> ProfileScreen(
                user = user,
                onSignOutClick = onSignOut,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

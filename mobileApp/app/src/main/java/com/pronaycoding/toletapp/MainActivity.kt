package com.pronaycoding.toletapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.pronaycoding.toletapp.auth.GoogleAuthManager
import com.pronaycoding.toletapp.ui.auth.SignInScreen
import com.pronaycoding.toletapp.ui.profile.ProfileScreen
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
    val coroutineScope = rememberCoroutineScope()

    var currentUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var isSigningIn by rememberSaveable { mutableStateOf(false) }
    var signInError by rememberSaveable { mutableStateOf<String?>(null) }

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

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label,
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it },
                )
            }
        },
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            when (currentDestination) {
                AppDestinations.HOME -> Greeting(
                    name = user.displayName ?: "there",
                    modifier = Modifier.padding(innerPadding),
                )

                AppDestinations.FAVORITES -> Greeting(
                    name = "Favorites",
                    modifier = Modifier.padding(innerPadding),
                )

                AppDestinations.PROFILE -> ProfileScreen(
                    user = user,
                    onSignOutClick = { authManager.signOut() },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HOME("Home", R.drawable.ic_home),
    FAVORITES("Favorites", R.drawable.ic_favorite),
    PROFILE("Profile", R.drawable.ic_account_box),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier.padding(24.dp),
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToletAppTheme {
        Greeting("Android")
    }
}

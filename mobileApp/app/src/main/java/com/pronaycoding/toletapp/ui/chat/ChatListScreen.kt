package com.pronaycoding.toletapp.ui.chat

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseUser
import com.pronaycoding.toletapp.R
import com.pronaycoding.toletapp.data.ChatRepository
import com.pronaycoding.toletapp.data.UserRepository
import com.pronaycoding.toletapp.data.model.ChatConversation
import com.pronaycoding.toletapp.data.model.UserProfile
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatListScreen(
    user: FirebaseUser,
    onChatClick: (UserProfile) -> Unit,
    modifier: Modifier = Modifier,
    chatRepository: ChatRepository = remember { ChatRepository() },
    userRepository: UserRepository = remember { UserRepository() },
) {
    var conversations by remember { mutableStateOf<List<ChatConversation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showNewChatDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun loadConversations() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            chatRepository.getConversations(user.uid)
                .onSuccess { rawConversations ->
                    conversations = rawConversations.map { conversation ->
                        val profileResult = userRepository.getUserProfile(conversation.otherUserId)
                        conversation.copy(
                            otherUser = profileResult.getOrNull()
                                ?: UserProfile(
                                    userId = conversation.otherUserId,
                                    displayName = "User",
                                ),
                        )
                    }
                }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    LaunchedEffect(user.uid) {
        loadConversations()
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewChatDialog = true },
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.new_chat))
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.chat_title),
                style = MaterialTheme.typography.headlineSmall,
            )

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

                conversations.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.no_conversations),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 24.dp),
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(bottom = 80.dp),
                    ) {
                        items(conversations, key = { it.otherUserId }) { conversation ->
                            ConversationRow(
                                conversation = conversation,
                                onClick = { conversation.otherUser?.let(onChatClick) },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showNewChatDialog) {
        NewChatDialog(
            currentUserId = user.uid,
            userRepository = userRepository,
            onDismiss = { showNewChatDialog = false },
            onUserFound = { profile ->
                showNewChatDialog = false
                onChatClick(profile)
            },
        )
    }
}

@Composable
private fun ConversationRow(
    conversation: ChatConversation,
    onClick: () -> Unit,
) {
    val profile = conversation.otherUser
    val lastMessage = conversation.lastMessage
    val timeFormatter = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = profile?.displayName?.ifBlank { "User" } ?: "User",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = lastMessage?.text ?: stringResource(R.string.no_messages_yet),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        if (lastMessage != null && lastMessage.timestamp > 0L) {
            Text(
                text = timeFormatter.format(Date(lastMessage.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Composable
private fun NewChatDialog(
    currentUserId: String,
    userRepository: UserRepository,
    onDismiss: () -> Unit,
    onUserFound: (UserProfile) -> Unit,
) {
    var phone by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.new_chat)) },
        text = {
            Column {
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        error = null
                    },
                    label = { Text(stringResource(R.string.new_chat_phone_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isSearching) return@TextButton
                    coroutineScope.launch {
                        isSearching = true
                        error = null
                        userRepository.findUserByPhone(phone)
                            .onSuccess { profile ->
                                when {
                                    profile == null -> error = "User not found with that phone number."
                                    profile.userId == currentUserId -> error = "You cannot chat with yourself."
                                    else -> onUserFound(profile)
                                }
                            }
                            .onFailure { error = it.message }
                        isSearching = false
                    }
                },
                enabled = phone.isNotBlank() && !isSearching,
            ) {
                Text(stringResource(R.string.start_chat))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        },
    )
}

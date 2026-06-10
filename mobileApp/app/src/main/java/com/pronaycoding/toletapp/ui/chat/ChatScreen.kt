package com.pronaycoding.toletapp.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseUser
import com.pronaycoding.toletapp.data.ChatRepository
import com.pronaycoding.toletapp.data.model.ChatMessage
import com.pronaycoding.toletapp.data.model.UserProfile
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentUser: FirebaseUser,
    otherUser: UserProfile,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    chatRepository: ChatRepository = remember { ChatRepository() },
) {
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var messageText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSending by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun loadMessages() {
        coroutineScope.launch {
            isLoading = true
            chatRepository.getMessages(currentUser.uid, otherUser.userId)
                .onSuccess { messages = it }
            isLoading = false
        }
    }

    LaunchedEffect(otherUser.userId) {
        loadMessages()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(otherUser.displayName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    reverseLayout = true,
                ) {
                    items(messages.reversed(), key = { it.id }) { message ->
                        val isMine = message.senderId == currentUser.uid
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart,
                        ) {
                            Text(
                                text = message.text,
                                modifier = Modifier
                                    .background(
                                        color = if (isMine) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                color = if (isMine) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message") },
                    singleLine = true,
                )
                IconButton(
                    onClick = {
                        if (messageText.isBlank() || isSending) return@IconButton
                        coroutineScope.launch {
                            isSending = true
                            chatRepository.sendMessage(
                                currentUserId = currentUser.uid,
                                otherUserId = otherUser.userId,
                                text = messageText,
                            ).onSuccess {
                                messageText = ""
                                loadMessages()
                            }
                            isSending = false
                        }
                    },
                    enabled = messageText.isNotBlank() && !isSending,
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}

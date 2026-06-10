package com.pronaycoding.toletapp.ui.phone

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseUser
import com.pronaycoding.toletapp.R
import com.pronaycoding.toletapp.data.UserRepository
import com.pronaycoding.toletapp.ui.auth.AuthErrorBanner
import com.pronaycoding.toletapp.ui.auth.AuthScreenLayout
import kotlinx.coroutines.launch

@Composable
fun PhoneNumberScreen(
    user: FirebaseUser,
    modifier: Modifier = Modifier,
    userRepository: UserRepository = remember { UserRepository() },
    onPhoneSaved: () -> Unit,
) {
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var isSaving by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val displayName = user.displayName?.takeIf { it.isNotBlank() }
    val invalidPhoneMessage = stringResource(R.string.phone_invalid)
    val saveFailedMessage = stringResource(R.string.phone_save_failed)

    AuthScreenLayout(
        modifier = modifier,
        heroIcon = Icons.Outlined.Phone,
        stepLabel = stringResource(R.string.phone_step_label),
        title = stringResource(R.string.phone_required_title),
        subtitle = if (displayName != null) {
            stringResource(R.string.phone_required_subtitle_named, displayName)
        } else {
            stringResource(R.string.phone_required_subtitle)
        },
    ) {
        user.photoUrl?.toString()?.takeIf { it.isNotBlank() }?.let { photoUrl ->
            AsyncImage(
                model = photoUrl,
                contentDescription = displayName,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop,
            )
        }

        Text(
            text = stringResource(R.string.phone_card_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text(stringResource(R.string.phone_hint)) },
            placeholder = { Text(stringResource(R.string.phone_placeholder)) },
            prefix = {
                Text(
                    text = stringResource(R.string.phone_country_code),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
            ),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = "•",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.phone_privacy_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (isSaving) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp))
            }
        } else {
            Button(
                onClick = {
                    if (!UserRepository.isValidPhone(phoneNumber)) {
                        errorMessage = invalidPhoneMessage
                        return@Button
                    }
                    coroutineScope.launch {
                        isSaving = true
                        errorMessage = null
                        userRepository.savePhoneNumber(user, phoneNumber)
                            .onSuccess { onPhoneSaved() }
                            .onFailure {
                                errorMessage = it.message ?: saveFailedMessage
                            }
                        isSaving = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.large,
                enabled = phoneNumber.isNotBlank(),
            ) {
                Text(
                    text = stringResource(R.string.save_phone),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        errorMessage?.let { message ->
            AuthErrorBanner(message = message)
        }
    }
}

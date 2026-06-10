package com.pronaycoding.toletapp.ui.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseUser
import com.pronaycoding.toletapp.R
import com.pronaycoding.toletapp.data.ToletRepository
import com.pronaycoding.toletapp.data.model.ToletListing
import kotlinx.coroutines.launch

private val propertyTypes = listOf("Flat", "House", "Room", "Office")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToletScreen(
    user: FirebaseUser,
    modifier: Modifier = Modifier,
    repository: ToletRepository = remember { ToletRepository() },
    onListingPosted: () -> Unit = {},
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var bedrooms by remember { mutableStateOf("") }
    var propertyType by remember { mutableStateOf(propertyTypes.first()) }
    var propertyTypeExpanded by remember { mutableStateOf(false) }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(ToletRepository.MAX_IMAGES),
    ) { uris ->
        selectedImages = (selectedImages + uris).take(ToletRepository.MAX_IMAGES)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.post_tolet_title),
            style = MaterialTheme.typography.headlineSmall,
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.title_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            singleLine = true,
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.description_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            minLines = 3,
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text(stringResource(R.string.location_hint)) },
            placeholder = { Text(stringResource(R.string.location_placeholder)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            singleLine = true,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text(stringResource(R.string.price_hint)) },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )

            OutlinedTextField(
                value = bedrooms,
                onValueChange = { bedrooms = it },
                label = { Text(stringResource(R.string.bedrooms_hint)) },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }

        ExposedDropdownMenuBox(
            expanded = propertyTypeExpanded,
            onExpandedChange = { propertyTypeExpanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
        ) {
            OutlinedTextField(
                value = propertyType,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.property_type_hint)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = propertyTypeExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = propertyTypeExpanded,
                onDismissRequest = { propertyTypeExpanded = false },
            ) {
                propertyTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            propertyType = type
                            propertyTypeExpanded = false
                        },
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.images_label, selectedImages.size, ToletRepository.MAX_IMAGES),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 16.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            selectedImages.forEach { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        OutlinedButton(
            onClick = {
                imagePickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            },
            enabled = selectedImages.size < ToletRepository.MAX_IMAGES,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Text(stringResource(R.string.add_images))
        }

        if (isSubmitting) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            Button(
                onClick = {
                    when {
                        title.isBlank() -> errorMessage = "Title is required."
                        location.isBlank() -> errorMessage = "Location is required."
                        price.isBlank() -> errorMessage = "Price is required."
                        else -> {
                            coroutineScope.launch {
                                isSubmitting = true
                                errorMessage = null
                                successMessage = null

                                val listing = ToletListing(
                                    userId = user.uid,
                                    userName = user.displayName ?: "User",
                                    title = title.trim(),
                                    description = description.trim(),
                                    location = location.trim(),
                                    locationLower = location.trim().lowercase(),
                                    price = price.trim(),
                                    bedrooms = bedrooms.trim().ifBlank { "N/A" },
                                    propertyType = propertyType,
                                    createdAt = System.currentTimeMillis(),
                                )

                                repository.createListing(context, listing, selectedImages)
                                    .onSuccess {
                                        successMessage = "To-let posted successfully!"
                                        title = ""
                                        description = ""
                                        location = ""
                                        price = ""
                                        bedrooms = ""
                                        selectedImages = emptyList()
                                        onListingPosted()
                                    }
                                    .onFailure {
                                        errorMessage = it.message ?: "Failed to post listing."
                                    }

                                isSubmitting = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            ) {
                Text(stringResource(R.string.post_listing))
            }
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        successMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

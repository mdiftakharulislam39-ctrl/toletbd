package com.pronaycoding.toletapp.ui.add

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.pronaycoding.toletapp.data.model.ToletListing
import com.pronaycoding.toletapp.domain.repository.ToletRepository
import com.pronaycoding.toletapp.ui.common.ListingImage
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToletScreen(
    user: FirebaseUser,
    modifier: Modifier = Modifier,
    listingToEdit: ToletListing? = null,
    onListingPosted: () -> Unit = {},
    onBack: (() -> Unit)? = null,
    viewModel: AddToletViewModel = koinViewModel { parametersOf(user, listingToEdit) },
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(ToletRepository.MAX_IMAGES),
    ) { uris ->
        viewModel.addSelectedImages(uris)
    }

    val formContent: @Composable () -> Unit = {
        Text(
            text = stringResource(
                if (viewModel.isEditMode) R.string.edit_tolet_title else R.string.post_tolet_title,
            ),
            style = MaterialTheme.typography.headlineSmall,
        )

        OutlinedTextField(
            value = uiState.title,
            onValueChange = viewModel::onTitleChange,
            label = { Text(stringResource(R.string.title_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            singleLine = true,
        )

        OutlinedTextField(
            value = uiState.description,
            onValueChange = viewModel::onDescriptionChange,
            label = { Text(stringResource(R.string.description_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            minLines = 3,
        )

        OutlinedTextField(
            value = uiState.location,
            onValueChange = viewModel::onLocationChange,
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
                value = uiState.price,
                onValueChange = viewModel::onPriceChange,
                label = { Text(stringResource(R.string.price_hint)) },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )

            OutlinedTextField(
                value = uiState.bedrooms,
                onValueChange = viewModel::onBedroomsChange,
                label = { Text(stringResource(R.string.bedrooms_hint)) },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }

        ExposedDropdownMenuBox(
            expanded = uiState.propertyTypeExpanded,
            onExpandedChange = viewModel::onPropertyTypeExpandedChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
        ) {
            OutlinedTextField(
                value = uiState.propertyType,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.property_type_hint)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.propertyTypeExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = uiState.propertyTypeExpanded,
                onDismissRequest = { viewModel.onPropertyTypeExpandedChange(false) },
            ) {
                propertyTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            viewModel.onPropertyTypeChange(type)
                            viewModel.onPropertyTypeExpandedChange(false)
                        },
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.images_label, uiState.totalImageCount, ToletRepository.MAX_IMAGES),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 16.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            uiState.existingImages.forEachIndexed { index, imageData ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                ) {
                    ListingImage(
                        imageData = imageData,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    if (viewModel.isEditMode) {
                        IconButton(
                            onClick = { viewModel.removeExistingImage(index) },
                            modifier = Modifier.align(Alignment.TopEnd),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.remove_image),
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }
            uiState.selectedImages.forEachIndexed { index, uri ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    IconButton(
                        onClick = { viewModel.removeSelectedImage(index) },
                        modifier = Modifier.align(Alignment.TopEnd),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.remove_image),
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }

        OutlinedButton(
            onClick = {
                imagePickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            },
            enabled = uiState.totalImageCount < ToletRepository.MAX_IMAGES,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Text(stringResource(R.string.add_images))
        }

        if (uiState.isSubmitting) {
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
                onClick = { viewModel.submit(context, onListingPosted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            ) {
                Text(
                    stringResource(
                        if (viewModel.isEditMode) R.string.save_listing_changes else R.string.post_listing,
                    ),
                )
            }
        }

        uiState.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        uiState.successMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }

    if (viewModel.isEditMode && onBack != null) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.edit_tolet_title)) },
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
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(16.dp),
            ) {
                formContent()
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            formContent()
        }
    }
}

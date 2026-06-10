package com.pronaycoding.toletapp.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.pronaycoding.toletapp.data.ImageEncoder

@Composable
fun ListingImage(
    imageData: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val model = remember(imageData) {
        if (imageData.startsWith("http")) {
            imageData
        } else {
            ImageEncoder.decodeFromBase64(imageData)
        }
    }

    AsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
    )
}

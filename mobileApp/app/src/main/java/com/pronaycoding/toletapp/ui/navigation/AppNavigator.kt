package com.pronaycoding.toletapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.pronaycoding.toletapp.data.model.ToletListing
import com.pronaycoding.toletapp.data.model.UserProfile

sealed class OverlayDestination {
    data class ListingDetail(val listingId: String) : OverlayDestination()
    data class Chat(val otherUserId: String) : OverlayDestination()
}

class AppNavigator(
    val stack: SnapshotStateList<OverlayDestination>,
) {
    val currentOverlay: OverlayDestination?
        get() = stack.lastOrNull()

    val canPopOverlay: Boolean
        get() = stack.isNotEmpty()

    fun openListing(listing: ToletListing) {
        stack.add(OverlayDestination.ListingDetail(listing.id))
    }

    fun openChat(user: UserProfile) {
        stack.add(OverlayDestination.Chat(user.userId))
    }

    fun popOverlay() {
        if (stack.isNotEmpty()) {
            stack.removeAt(stack.lastIndex)
        }
    }

    fun clearOverlays() {
        stack.clear()
    }
}

@Composable
fun rememberAppNavigator(): AppNavigator {
    val stack = remember { mutableStateListOf<OverlayDestination>() }
    return remember { AppNavigator(stack) }
}

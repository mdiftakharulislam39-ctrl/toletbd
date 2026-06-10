package com.pronaycoding.toletapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestinations(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val isCenterAction: Boolean = false,
) {
    HOME(
        label = "Search",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search,
    ),
    SAVED(
        label = "Saved",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
    ),
    POST(
        label = "Post",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Filled.Add,
        isCenterAction = true,
    ),
    PROFILE(
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
    ),
    ;

    companion object {
        val bottomBarItems: List<AppDestinations> = listOf(HOME, SAVED, POST, PROFILE)
    }
}

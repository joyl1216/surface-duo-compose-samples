/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.device.display.samples.navigationrail.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.microsoft.device.display.samples.navigationrail.models.DataProvider
import com.microsoft.device.display.samples.navigationrail.ui.components.GalleryBottomNav
import com.microsoft.device.display.samples.navigationrail.ui.components.ItemTopBar

@ExperimentalAnimationApi
@ExperimentalUnitApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun NavigationRailApp() {
    // Set up starting route for navigation in pane 1
    var currentRoute by rememberSaveable { mutableStateOf(navDestinations[0].route) }
    val updateRoute: (String) -> Unit = { newRoute -> currentRoute = newRoute }

    // Set up variable to store selected image id
    var imageId: Int? by rememberSaveable { mutableStateOf(null) }
    val updateImageId: (Int?) -> Unit = { newId -> imageId = newId }

    NavigationRailAppContent(
        imageId = imageId,
        updateImageId = updateImageId,
        currentRoute = currentRoute,
        updateRoute = updateRoute
    )
}

@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun NavigationRailAppContent(
    imageId: Int?,
    updateImageId: (Int?) -> Unit,
    currentRoute: String,
    updateRoute: (String) -> Unit
) {
    val navController = rememberNavController()
    SinglePaneLayout(navController, imageId, updateImageId, currentRoute, updateRoute)
}

@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
internal fun SinglePaneLayout(
    navHostController: NavHostController,
    imageId: Int?,
    updateImageId: (Int?) -> Unit,
    currentRoute: String,
    updateRoute: (String) -> Unit
) {
    val selectImage: (Int?) -> Unit = { newId ->
        updateImageId(newId)
        navHostController.navigate(DETAIL_ROUTE)
    }

    val unselectImage: () -> Unit = {
        updateImageId(null)
        navHostController.popBackStack()
    }

    val inGalleryDestination = navHostController.currentDestination?.route != DETAIL_ROUTE

    Scaffold(
        bottomBar = {
            if (inGalleryDestination)
                GalleryBottomNav(navHostController, navDestinations, updateImageId, updateRoute)
        },
    ) { paddingValues ->
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navHostController,
            startDestination = navDestinations.first().route
        ) {
            addGalleryGraph(imageId, selectImage, GALLERY_HORIZ_PADDING)
            composable(DETAIL_ROUTE) {
                Pane2(imageId, unselectImage, currentRoute)
            }
        }
    }
}

@ExperimentalUnitApi
@ExperimentalMaterialApi
@Composable
fun Pane2(
    imageId: Int?,
    unselectImage: () -> Unit,
    currentRoute: String,
) {
    // Retrieve selected image information
    val selectedImage = imageId?.let { DataProvider.getImage(imageId) }

    // Set up back press action to return to pane 1 and clear image selection
    val onBackPressed = {
        unselectImage()
    }
    BackHandler { onBackPressed() }

    ItemDetailView(
        selectedImage = selectedImage,
        currentRoute = currentRoute
    )
    // Show a "back" icon
    ItemTopBar(
        onClick = { onBackPressed() }
    )
}

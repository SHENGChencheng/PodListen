package com.podlisten.android.mobile.ui.player

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import androidx.window.layout.DisplayFeature

@Composable
fun PlayerScreen(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    onBackPress: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {

}
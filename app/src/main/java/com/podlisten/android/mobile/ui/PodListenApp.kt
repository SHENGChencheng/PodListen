package com.podlisten.android.mobile.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.window.layout.DisplayFeature
import com.podlisten.android.R
import com.podlisten.android.mobile.ui.home.MainScreen
import com.podlisten.android.mobile.ui.player.PlayerScreen

@Composable
fun PodListenApp(
    displayFeatures: List<DisplayFeature>,
    appState: PodListenAppState = rememberPodListenAppState(),
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    if (appState.isOnline) {
        NavHost(
            navController = appState.navController,
            startDestination = Screen.Home.route,
        ) {
            composable(Screen.Home.route) { backStackEntry ->
                MainScreen(
                    windowSizeClass = adaptiveInfo.windowSizeClass,
                    navigateToPlayer = { episode ->
                        appState.navigateToPlayer(episode.uri, backStackEntry)
                    }
                )
            }
            composable((Screen.Player.route)) {
                PlayerScreen(
                    windowSizeClass = adaptiveInfo.windowSizeClass,
                    displayFeatures = displayFeatures,
                    onBackPress = appState::navigateBack,
                )
            }
        }
    } else {
        OfflineDialog { appState.refreshOnline() }
    }
}

@Composable
fun OfflineDialog(onRetry: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(R.string.connection_error_title)) },
        text = { Text(text = stringResource(R.string.connection_error_message)) },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(text = stringResource(R.string.retry_label))
            }
        }
    )
}
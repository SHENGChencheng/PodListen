package com.podlisten.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.accompanist.adaptive.calculateDisplayFeatures
import com.podlisten.android.mobile.ui.PodListenApp
import com.podlisten.android.ui.theme.PodListenTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val displayFeatures = calculateDisplayFeatures(this)

            PodListenTheme {
                PodListenApp(
                    displayFeatures
                )
            }
        }
    }
}
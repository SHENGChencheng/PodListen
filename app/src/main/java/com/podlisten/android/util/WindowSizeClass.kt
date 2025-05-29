package com.podlisten.android.util

import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

val WindowSizeClass.isCompact: Boolean
    get() = windowWidthSizeClass == WindowWidthSizeClass.COMPACT ||
            windowHeightSizeClass == WindowHeightSizeClass.COMPACT
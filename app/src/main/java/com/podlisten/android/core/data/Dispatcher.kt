package com.podlisten.android.core.data

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val podListenDispatcher: PodListenDispatcher)

enum class PodListenDispatcher {
    Main,
    IO,
}
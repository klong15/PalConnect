package com.example.palconnect

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Config : Route

    @Serializable
    data object Overview : Route
}

object NavigationManager {
    private val _route = MutableSharedFlow<Route>()
    val route: SharedFlow<Route> = _route

    fun navigateTo(event: Route) {
        _route.tryEmit(event)
    }

    suspend fun navigateToAsync(event: Route) {
        _route.emit(event)
    }
}
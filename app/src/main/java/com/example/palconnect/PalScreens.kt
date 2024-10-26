package com.example.palconnect

import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Config : Route

    @Serializable
    data object Overview : Route

    @Serializable
    data object Players : Route
}

class NavigationManager {
    private val _route = MutableSharedFlow<Route>()
    val route: SharedFlow<Route> = _route

    fun navigateTo(event: Route) {
        _route.tryEmit(event)
    }

    suspend fun navigateToAsync(event: Route) {
        _route.emit(event)
    }
}

fun Modifier.conditional(condition : Boolean, modifier : Modifier.() -> Modifier) : Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}
package com.example.palconnect

import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.Serializable

sealed interface Route {

    val backButtonCallback: (() -> Unit)?
    val showBackButtonInNavBar: Boolean

    @Serializable
    data object Config : Route {
        override val showBackButtonInNavBar: Boolean = false
        override val backButtonCallback: (() -> Unit)?
            get() = {  }
    }

    @Serializable
    data object Overview : Route {
        override val showBackButtonInNavBar: Boolean = false
        override val backButtonCallback: (() -> Unit)?
            get() = null
    }

    @Serializable
    data object Players : Route {
        override val showBackButtonInNavBar: Boolean = true
        override val backButtonCallback: (() -> Unit)?
            get() = null
    }

    @Serializable
    data object PopBackStack: Route {
        override val showBackButtonInNavBar: Boolean = false
        override val backButtonCallback: (() -> Unit)?
            get() = null
    }
}

val Route.name: String?
    get() = this::class.qualifiedName

class NavigationManager {
    private val _route = MutableSharedFlow<Route>(
        extraBufferCapacity = 1
    )
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
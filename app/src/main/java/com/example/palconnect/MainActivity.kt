package com.example.palconnect

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.palconnect.ui.config.ConfigScreen
import com.example.palconnect.ui.overview.OverviewScreen
import com.example.palconnect.ui.players.PlayersScreen
import com.example.palconnect.ui.theme.PalMyTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            PalApp(windowSizeClass)
        }
    }
}

enum class NavBarAction() {
    Settings
}

fun getInfoByRoute(
    context: Context,
    route:String?,
    showBackIcon: MutableState<Boolean>,
    screenBackButtonCallback: MutableState<(() -> Unit)?>,
    screenType: MutableState<Route>
) {
    if(route == null) return

    val a = Route.Config::class.qualifiedName
    when (route) {
        Route.Config.name -> {
            screenBackButtonCallback.value = Route.Config.backButtonCallback
            showBackIcon.value = Route.Config.showBackButtonInNavBar
            screenType.value = Route.Config
        }
        Route.Overview.name -> {
            screenBackButtonCallback.value = Route.Overview.backButtonCallback
            showBackIcon.value = Route.Overview.showBackButtonInNavBar
            screenType.value = Route.Overview
        }
        Route.Players.name -> {
            screenBackButtonCallback.value = Route.Players.backButtonCallback
            showBackIcon.value = Route.Players.showBackButtonInNavBar
            screenType.value = Route.Players
        }
    }
}

@Composable
fun NavigatorLaunchedEffect(
    navController: NavHostController,
) {
    LaunchedEffect("NavigationEvents") {
        PalConnectApp.palModule.palNavigationManager.route.collect { screen ->
            if(screen == Route.PopBackStack) {
                navController.popBackStack()
            } else {
                navController.navigate(screen)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PalApp(
    windowSize: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    var dynamicColor = PalConnectApp.palModule.palDataStore.dynamicColorFlow.collectAsState(false)

    PalMyTheme(
        dynamicColor = dynamicColor.value
    ) {
        val context = LocalContext.current
        val navController = rememberNavController()
        var topBarTitle = remember { mutableStateOf("") }
        var showBackIcon = remember { mutableStateOf(true) }
        var curScreenBackButton = remember { mutableStateOf<(()->Unit)?>(null) }
        var screenType = remember { mutableStateOf<Route>(Route.Config) }
        var actionClickFlow = remember { MutableSharedFlow<NavBarAction>(
            extraBufferCapacity = 1
        ) }
        LaunchedEffect(navController) {
            navController.currentBackStackEntryFlow.collect { backStackEntry ->
                // You can map the title based on the route using:
                getInfoByRoute(context, backStackEntry.destination.route, showBackIcon, curScreenBackButton, screenType)
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
                    title = {
                        Text(text = topBarTitle.value)
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .alpha(if (showBackIcon.value) 1f else 0f)
                                .clickable(
                                    enabled = showBackIcon.value,
                                    onClick = {
                                        curScreenBackButton.value?.invoke()
                                            ?: navController.popBackStack()
                                    }
                                )
                                .padding(4.dp)

                        )
                    },
                    actions = {
                        NavBarActions(
                            route = screenType.value,
                            actionsFlow = actionClickFlow,
                            dynamicColor = dynamicColor.value
                        )
                    }
                )
            },
        ) { innerPadding ->

            NavigatorLaunchedEffect(navController = navController)
            PalNavHost(
                navController = navController,
                topBarTitle = topBarTitle,
                navBarActionsFlow = actionClickFlow,
                modifier = modifier.padding(innerPadding),
                windowSize = windowSize
            )
        }
    }
}

@Composable
fun PalNavHost(
    navController: NavHostController,
    topBarTitle: MutableState<String>,
    navBarActionsFlow: SharedFlow<NavBarAction>,
    windowSize: WindowSizeClass,
    modifier: Modifier = Modifier
) {

    NavHost (
        navController = navController,
        startDestination = Route.Overview,
        modifier = modifier
    ) {
        composable<Route.Config>{
            BackHandler(true) { Route.Config.backButtonCallback }
            ConfigScreen(
                topBarTitle = topBarTitle
            )
        }
        composable<Route.Overview> {
            OverviewScreen(
                topBarTitle = topBarTitle,
                navBarActionsFlow = navBarActionsFlow,
                windowSize = windowSize
            )
        }
        composable<Route.Players> {
            PlayersScreen(
                topBarTitle = topBarTitle
            )
        }
    }
}

@Composable
fun NavBarActions(
    route: Route,
    actionsFlow: MutableSharedFlow<NavBarAction>,
    dynamicColor: Boolean
) {
    val composableScope =  rememberCoroutineScope()
    val context = LocalContext.current
    Icon(
        imageVector = if(dynamicColor) Icons.Filled.ChangeHistory else Icons.Filled.ChangeCircle,
        contentDescription = "Localized description",
        modifier = Modifier
            .clickable(
                enabled = true,
                onClick = {
                    composableScope.launch {
                        PalConnectApp.palModule.palDataStore.saveDynamicColorPreference(!dynamicColor)
                    }
                    val msgId = if (!dynamicColor) R.string.dynamic_colors_on_toast else R.string.dynamic_colors_off_toast
                    Toast
                        .makeText(
                            context,
                            context.getString(msgId),
                            Toast.LENGTH_SHORT
                        ).show()
                }
            )
            .padding(horizontal = 8.dp)
    )
    
    if(route == Route.Overview){
        Icon(
            imageVector = Icons.Filled.Dns,
            contentDescription = "Localized description",
            modifier = Modifier
                .clickable(
                    enabled = true,
                    onClick = {
                        actionsFlow.tryEmit(NavBarAction.Settings)
                    }
                )
                .padding(horizontal = 8.dp)
        )
    }
}




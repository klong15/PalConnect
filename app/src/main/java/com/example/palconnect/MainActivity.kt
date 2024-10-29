package com.example.palconnect

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.palconnect.ui.config.ConfigScreen
import com.example.palconnect.ui.overview.OverviewScreen
import com.example.palconnect.ui.players.PlayersScreen
import com.example.palconnect.ui.theme.PalConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PalApp()
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

fun getBackButtonInfoByRoute(
    context: Context,
    route:String?,
    showBackIcon: MutableState<Boolean>,
    screenBackButtonCallback: MutableState<(() -> Unit)?>
) {
    if(route == null) return

    val a = Route.Config::class.qualifiedName
    when (route) {
        Route.Config.name -> {
            screenBackButtonCallback.value = Route.Config.backButtonCallback
            showBackIcon.value = Route.Config.showBackButtonInNavBar
        }
        Route.Overview.name -> {
            screenBackButtonCallback.value = Route.Overview.backButtonCallback
            showBackIcon.value = Route.Overview.showBackButtonInNavBar
        }
        Route.Players.name -> {
            screenBackButtonCallback.value = Route.Players.backButtonCallback
            showBackIcon.value = Route.Players.showBackButtonInNavBar
        }
        // other cases
        else -> {
            screenBackButtonCallback.value = null
            showBackIcon.value = true;
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PalApp(modifier: Modifier = Modifier) {
    PalConnectTheme {
        val context = LocalContext.current
        val navController = rememberNavController()
        var topBarTitle = remember { mutableStateOf("") }
        var showBackIcon = remember { mutableStateOf(true) }
        var curScreenBackButton = remember { mutableStateOf<(()->Unit)?>(null) }

        LaunchedEffect(navController) {
            navController.currentBackStackEntryFlow.collect { backStackEntry ->
                // You can map the title based on the route using:
                getBackButtonInfoByRoute(context, backStackEntry.destination.route, showBackIcon, curScreenBackButton)
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
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
                                      curScreenBackButton.value?.invoke() ?: navController.popBackStack()
                                  }
                                )
                                .padding(4.dp)

                        )
                    }
                )
            },
        ) { innerPadding ->

            NavigatorLaunchedEffect(navController = navController)
            PalNavHost(
                navController = navController,
                topBarTitle = topBarTitle,
                modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun PalNavHost(
    navController: NavHostController,
    topBarTitle: MutableState<String>,
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
                topBarTitle = topBarTitle
            )
        }
        composable<Route.Players> {
            PlayersScreen(
                topBarTitle = topBarTitle
            )
        }
    }
}






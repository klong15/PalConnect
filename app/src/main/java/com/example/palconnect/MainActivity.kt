package com.example.palconnect

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
            navController.navigate(screen)
        }
    }
}

fun getTitleByRoute(context: Context, route:String?): String {
    if(route == null) return ""
    val a = Route.Config::class.qualifiedName
    return when (route) {
        Route.Config.name -> "Config"
        Route.Overview.name -> "Overview"
        Route.Players.name -> "Players"
        // other cases
        else -> context.getString(R.string.error_load_players)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PalApp(modifier: Modifier = Modifier) {
    PalConnectTheme {
        val context = LocalContext.current
        val navController = rememberNavController()
        var topBarTitle by remember { mutableStateOf("") }

        LaunchedEffect(navController) {
            navController.currentBackStackEntryFlow.collect { backStackEntry ->
                // You can map the title based on the route using:
                topBarTitle = getTitleByRoute(context, backStackEntry.destination.route)
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
                        Text(text = topBarTitle)
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->

            NavigatorLaunchedEffect(navController = navController)
            PalNavHost(
                navController = navController,
                modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun PalNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost (
        navController = navController,
        startDestination = Route.Config,
        modifier = modifier
    ) {
//        composable(route = Route.Config.title){
//            ConfigScreen()
//        }
//        composable(route = Route.Overview.title) {
//            OverviewScreen()
//        }
//        composable(route = Route.Players.title) {
//            PlayersScreen()
//        }

        composable<Route.Config>{
            ConfigScreen()
        }
        composable<Route.Overview> {
            OverviewScreen()
        }
        composable<Route.Players> {
            PlayersScreen()
        }
    }
}






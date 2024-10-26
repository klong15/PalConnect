package com.example.palconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.palconnect.models.ServerInfoModel
import com.example.palconnect.ui.config.ConfigScreen
import com.example.palconnect.ui.overview.OverviewScreen
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

@Composable
fun PalApp(modifier: Modifier = Modifier) {
    PalConnectTheme {
        val navController = rememberNavController()

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

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
        composable<Route.Config>{
            ConfigScreen()
        }
        composable<Route.Overview> {
            OverviewScreen()
        }
    }
}






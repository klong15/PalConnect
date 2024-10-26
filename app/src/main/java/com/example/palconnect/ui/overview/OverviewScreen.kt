package com.example.palconnect.ui.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.palconnect.PalConnectApp
import com.example.palconnect.Route
import com.example.palconnect.models.ServerInfoModel
import com.example.palconnect.ui.theme.PalConnectTheme
import com.example.palconnect.viewmodels.OverviewUiState
import com.example.palconnect.viewmodels.OverviewViewModel
import com.example.palconnect.viewmodels.viewModelFactory
import kotlinx.coroutines.delay

@Composable
fun OverviewScreen(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: OverviewViewModel = viewModel(
        factory = viewModelFactory {
            OverviewViewModel(PalConnectApp.palModule.palApiService, PalConnectApp.palModule.palNavigationManager)
        }
    ),
) {
    // Feed lifecycle events back into viewmodel
    val currentOnStart by rememberUpdatedState(viewModel::onStart)
    val currentOnStop by rememberUpdatedState(viewModel::onStop)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                currentOnStart(Route.Overview)
            } else if (event == Lifecycle.Event.ON_STOP) {
                currentOnStop(Route.Overview)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    val uiState by viewModel.uiState.collectAsState()
    OverviewContent(
        uiState = uiState,
    )
}

@Composable
fun OverviewContent(
    modifier: Modifier = Modifier,
    uiState: OverviewUiState
) {

    Column (
        modifier = Modifier.fillMaxSize()
    ){
        Text(
            text = uiState.infoModel.servername,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = uiState.infoModel.description,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = "Frame Time: ${uiState.metricsModel.serverframetime}",
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OverviewPreview() {
    PalConnectTheme {
        OverviewContent(uiState = OverviewUiState(
            infoModel = ServerInfoModel(
                servername = "Klong's Server",
                description = "We once sailed across the jade ocean. Only to be met by an orange whale with 2 thumbs."
            )
        ))
    }
}
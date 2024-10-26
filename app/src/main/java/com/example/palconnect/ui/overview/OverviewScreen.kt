package com.example.palconnect.ui.overview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.palconnect.PalConnectApp
import com.example.palconnect.ui.theme.PalConnectTheme
import com.example.palconnect.viewmodels.ConfigUiState
import com.example.palconnect.viewmodels.MainViewModel
import com.example.palconnect.viewmodels.viewModelFactory

@Composable
fun OverviewScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(
        factory = viewModelFactory {
            MainViewModel(PalConnectApp.palApiModule.palApiService)
        }
    ),
) {
    val configUiState by viewModel.configUiState.collectAsState()
    OverviewContent(
        configUiState = configUiState
    )
}

@Composable
fun OverviewContent(
    modifier: Modifier = Modifier,
    configUiState: ConfigUiState
) {
    Box (
        modifier = Modifier.fillMaxSize()
    ){
        Text(
            text = "OverviewScreen",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OverviewPreview() {
    PalConnectTheme {
        OverviewContent(configUiState = ConfigUiState())
    }
}
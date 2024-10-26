package com.example.palconnect.ui.overview

import android.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.palconnect.PalConnectApp
import com.example.palconnect.models.ServerInfoModel
import com.example.palconnect.ui.theme.PalConnectTheme
import com.example.palconnect.viewmodels.ConfigUiState
import com.example.palconnect.viewmodels.ConfigViewModel
import com.example.palconnect.viewmodels.OverviewUiState
import com.example.palconnect.viewmodels.OverviewViewModel
import com.example.palconnect.viewmodels.viewModelFactory

@Composable
fun OverviewScreen(
    modifier: Modifier = Modifier,
    viewModel: OverviewViewModel = viewModel(
        factory = viewModelFactory {
            OverviewViewModel(PalConnectApp.palModule.palApiService, PalConnectApp.palModule.palNavigationManager)
        }
    ),
) {
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
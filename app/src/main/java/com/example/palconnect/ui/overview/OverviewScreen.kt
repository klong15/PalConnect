package com.example.palconnect.ui.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

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
        makeAnnouncementClicked = viewModel::makeAnnouncementClicked,
        announcementResponseMessage = viewModel.announcementResponseMessage
    )
}

@Composable
fun OverviewContent(
    modifier: Modifier = Modifier,
    uiState: OverviewUiState,
    announcementResponseMessage: SharedFlow<String>,
    makeAnnouncementClicked: (String) -> Unit = {}
) {
    // Alert Dialog things
    var openDialog by remember { mutableStateOf(false) }

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
        ElevatedButton(onClick = { openDialog = true } ) {
            Text("Save World")
        }
    }

    when {
        openDialog -> {
            AnnounceDialog(
                makeAnnouncementClicked = makeAnnouncementClicked,
                onDismissRequest = { openDialog = false },
                uiState = uiState,
                announcementResponseMessage = announcementResponseMessage
            )
        }
    }
}

@Composable
fun AnnounceDialog(
    makeAnnouncementClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    announcementResponseMessage: SharedFlow<String>,
    uiState: OverviewUiState,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {

        Card (
            modifier = Modifier.fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 0.dp)
        ){
            var announceResponse by remember { mutableStateOf("") }
            LaunchedEffect("AnnounceResponse") {
                announcementResponseMessage.collect { msg ->
                    announceResponse = msg
                }
            }

            if(uiState.awaitingAnnounceResponse){
                Box (
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp)
                    )
                }
            } else {

                if(announceResponse.isNotEmpty()) {

                    Box (
                        modifier = Modifier.fillMaxSize(),
                    ){
                        Text(
                            text = announceResponse,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        ElevatedButton(
                            modifier = Modifier.padding(vertical = 16.dp)
                                .height(40.dp)
                                .align(Alignment.BottomCenter),
                            onClick = { onDismissRequest() }
                        ) {
                            Text("Dismiss")
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        var text by remember { mutableStateOf("") }
                        TextField(

                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 228.dp)
                                .padding(horizontal = 16.dp),
                            value = text,
                            placeholder = {
                                Text("Announcement")
                            },
                            onValueChange = { newText -> text = newText },
                            textStyle = MaterialTheme.typography.titleLarge,
                        )
                        ElevatedButton(
                            modifier = Modifier.padding(vertical = 16.dp)
                                .height(40.dp),
                            onClick = { makeAnnouncementClicked(text) }
                        ) {
                            Text("Make Announcement")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnnounceDialogPreview() {
    AnnounceDialog(
        uiState = OverviewUiState(awaitingAnnounceResponse = false),
        onDismissRequest = {},
        makeAnnouncementClicked = {},
        announcementResponseMessage = MutableSharedFlow<String>()
    )
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
            ),
            announcementResponseMessage = MutableSharedFlow<String>()
        )
    }
}
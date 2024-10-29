package com.example.palconnect.ui.overview

import android.content.Context
import android.content.res.Configuration
import android.icu.number.Precision
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.palconnect.NavBarAction
import com.example.palconnect.R
import com.example.palconnect.conditional
import com.example.palconnect.models.ServerInfoModel
import com.example.palconnect.models.ServerMetricsModel
import com.example.palconnect.ui.theme.PalMyTheme
import com.example.palconnect.viewmodels.OverviewUiState
import com.example.palconnect.viewmodels.OverviewViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.util.Locale

@Composable
fun OverviewScreen(
    modifier: Modifier = Modifier,
    topBarTitle: MutableState<String>,
    navBarActionsFlow: SharedFlow<NavBarAction>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: OverviewViewModel = viewModel(
        factory = OverviewViewModel.Factory
    ),
) {
    // Feed lifecycle events back into viewmodel
    val currentOnStart by rememberUpdatedState(viewModel::onStart)
    val currentOnStop by rememberUpdatedState(viewModel::onStop)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                currentOnStart()
            } else if (event == Lifecycle.Event.ON_STOP) {
                currentOnStop()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    if (uiState.pageTitle.isNotEmpty()) topBarTitle.value = uiState.pageTitle

    // Listen for nav bar button clicks
    LaunchedEffect("OverviewNavActions") {
        navBarActionsFlow.collect { type ->
            viewModel.navBarActionsClicked(type)
        }
    }

    // Content
    OverviewContent(
        uiState = uiState,
        makeAnnouncementClicked = viewModel::makeAnnouncementClicked,
        announcementResponseMessage = viewModel.announcementResponseMessage,
        saveWorldClicked = viewModel::saveWorldClicked,
        playersClicked = viewModel::playersClicked,
    )
}

@Composable
fun OverviewContent(
    modifier: Modifier = Modifier,
    uiState: OverviewUiState,
    announcementResponseMessage: SharedFlow<String>? = null,
    makeAnnouncementClicked: (String) -> Unit = {},
    saveWorldClicked: (Context) -> Unit = { context -> },
    playersClicked: () -> Unit = {},
) {
    // Alert Dialog things

    var openDialog = rememberSaveable { mutableStateOf(false) }

    if (uiState.isInitialLoading) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp)
            )
        }
    } else Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ServerInfo(
            modifier = Modifier, description = uiState.infoModel.description
        )
        MetricsInfo(
            curPlayers = uiState.metricsModel.currentplayernum,
            fps = uiState.metricsModel.serverfps,
            frameTime = uiState.metricsModel.serverframetime,
            maxPlayers = uiState.metricsModel.maxplayernum,
            upTime = uiState.metricsModel.uptime,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Spacer(
            modifier = Modifier.height(32.dp)
        )
        ActionsSection(
            openDialog = openDialog,
            saveWorldButtonEnable = uiState.saveWorldButtonEnable,
            modifier = Modifier,
            saveWorldClicked = saveWorldClicked,
            playersClicked = playersClicked
        )
    }

    when {
        openDialog.value -> {
            AnnounceDialog(
                makeAnnouncementClicked = makeAnnouncementClicked,
                onDismissRequest = { openDialog.value = false },
                uiState = uiState,
                announcementResponseMessage = announcementResponseMessage
            )
        }
    }
}

@Composable
fun ActionsSection(
    modifier: Modifier = Modifier,
    openDialog: MutableState<Boolean>,
    saveWorldButtonEnable: Boolean,
    saveWorldClicked: (Context) -> Unit = { context -> },
    playersClicked: () -> Unit = {},
) {
    Column {
        Text(
            text = stringResource(R.string.actions),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        Row {
            ElevatedButton(
                onClick = { openDialog.value = true },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 4.dp)
            ) {
                Text(
                    text = "Announce Message", overflow = TextOverflow.Ellipsis, maxLines = 1
                )
            }
            SaveWorldButton(
                enabled = saveWorldButtonEnable,
                saveWorldClicked = saveWorldClicked,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp, end = 8.dp)
            )
        }
        Row {
            ElevatedButton(
                onClick = playersClicked,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 4.dp)
            ) {
                Text(text = "Show Players")
            }
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp, end = 8.dp)
            )
        }
    }
}

@Composable
fun MetricsInfo(
    modifier: Modifier = Modifier,
    curPlayers: Int,
    fps: Int,
    frameTime: Float,
    maxPlayers: Int,
    upTime: Long,
) {

    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.metrics),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Card {
            Column {
                Row {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        MetricItem("Current Players", "$curPlayers")
                        MetricItem("Max Players", "$maxPlayers")
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        MetricItem("Frame Rate", "$fps")
                        MetricItem("Frame Time", frameTime.precision(2))
                    }
                }
                MetricItem(
                    metric = "Server Uptime",
                    value = "$upTime",
                    useWeight = false
                )
            }
        }
    }
}

@Composable
fun MetricItem(
    metric: String,
    value: String,
    useWeight: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(end = 8.dp)
    ) {
        Text(
            text = "$metric:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(4.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Right,
            modifier = Modifier
                .padding(4.dp)
                .conditional(useWeight, { weight(1f) })
        )
    }
}

@Composable
fun ServerInfo(
    modifier: Modifier = Modifier,
    description: String,
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.server_image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Text(
            text = description,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )
    }
}

@Composable
fun SaveWorldButton(
    modifier: Modifier = Modifier,
    saveWorldClicked: (Context) -> Unit,
    enabled: Boolean = true,
) {
    var context = LocalContext.current

    ElevatedButton(
        modifier = modifier, onClick = {
            saveWorldClicked(context)
        }, enabled = enabled
    ) {
        Text("Save World")
    }
}

@Composable
fun AnnounceDialog(
    makeAnnouncementClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    announcementResponseMessage: SharedFlow<String>?,
    uiState: OverviewUiState,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 0.dp)
        ) {
            var announceResponse by rememberSaveable { mutableStateOf("") }
            LaunchedEffect("AnnounceResponse") {
                announcementResponseMessage?.collect { msg ->
                    announceResponse = msg
                }
            }

            if (uiState.awaitingAnnounceResponse) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp)
                    )
                }
            } else {

                if (announceResponse.isNotEmpty()) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Text(
                            text = announceResponse, modifier = Modifier.align(Alignment.Center)
                        )
                        ElevatedButton(modifier = Modifier
                            .padding(vertical = 16.dp)
                            .height(40.dp)
                            .align(Alignment.BottomCenter), onClick = { onDismissRequest() }) {
                            Text("Dismiss")
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        var text by rememberSaveable { mutableStateOf("") }
                        var enableButton by rememberSaveable { mutableStateOf(false) }
                        TextField(

                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 228.dp)
                                .padding(horizontal = 16.dp),
                            value = text,
                            placeholder = {
                                Text("Announcement")
                            },
                            onValueChange = { newText ->
                                text = newText
                                enableButton = newText.isNotEmpty()
                            },
                            textStyle = MaterialTheme.typography.titleLarge,
                        )
                        ElevatedButton(modifier = Modifier
                            .padding(vertical = 16.dp)
                            .height(40.dp),
                            enabled = enableButton,
                            onClick = { makeAnnouncementClicked(text) }) {
                            Text("Make Announcement")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AnnounceDialogPreview() {
    PalMyTheme {
        AnnounceDialog(uiState = OverviewUiState(awaitingAnnounceResponse = false),
            onDismissRequest = {},
            makeAnnouncementClicked = {},
            announcementResponseMessage = MutableSharedFlow<String>()
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
fun OverviewPreview() {
    PalMyTheme {
        Surface {
            OverviewContent(uiState = OverviewUiState(
                infoModel = ServerInfoModel(
                    servername = "Klong's Server",
                    description = "We once sailed across the jade ocean. Only to be met by an orange whale with 2 thumbs."
                ),
                metricsModel = ServerMetricsModel(
                    serverframetime = 12.153422f
                ),
                isInitialLoading = false,
            ), saveWorldClicked = {})
        }
    }
}

fun Float.precision(precision: Int): String {
    return String.format(Locale.getDefault(), "%.${precision}f", this)
}
package com.example.palconnect.ui.players

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.palconnect.models.Player
import com.example.palconnect.models.PlayersModel
import com.example.palconnect.viewmodels.PlayersUiState
import com.example.palconnect.viewmodels.PlayersViewModel
import kotlinx.serialization.json.Json
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.palconnect.R

@Composable
fun PlayersScreen(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: PlayersViewModel = viewModel(
        factory = PlayersViewModel.Factory
    )
) {
    // Initial setup
    val uiState by viewModel.uiState.collectAsState()

    // Feed lifecycle events back into viewmodel
    val currentOnStart by rememberUpdatedState(viewModel::onStart)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                currentOnStart()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Content
    PlayersContent(
        modifier = modifier,
        uiState = uiState,
    )
}

@Composable
fun PlayersContent(
    modifier: Modifier = Modifier,
    uiState: PlayersUiState = PlayersUiState()
) {
    if(uiState.isLoadingData) {
        Box (
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp)
            )
        }
    } else if(uiState.errorMessage.isNotEmpty()) {
        Box (
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Text(text = uiState.errorMessage)
        }
    } else {
        var selectedPlayer = rememberSaveable { mutableStateOf("") }
        LazyColumn(
            modifier = Modifier
        ) {
            items(items = uiState.playersModel.players) { player ->
                PlayerCard(
                    player = player,
                    selectedPlayer = selectedPlayer
                )
            }
        }
    }
}

@Composable
fun PlayerCard(
    player: Player,
    selectedPlayer: MutableState<String> = mutableStateOf(""),
    modifier: Modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
) {
    Card(
        modifier = modifier
    ) {
        PlayerCardContent(
            player = player,
            selectedPlayer = selectedPlayer
        )
    }
}

@Composable
fun PlayerCardContent(
    player: Player,
    selectedPlayer: MutableState<String>,
    modifier: Modifier = Modifier,
) {

    val expanded = player.playerId == selectedPlayer.value
    Surface (
        color = MaterialTheme.colorScheme.primary,
        onClick = { selectedPlayer.value = if(selectedPlayer.value != player.playerId) player.playerId else ""}
    ){
        Column(
            modifier = Modifier
                .padding(12.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),

            ) {
            Row(
                modifier = Modifier
//                .weight(1f)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    modifier = Modifier.weight(1f)
                )


                Icon(
                    imageVector = if (expanded) Filled.ExpandLess else Filled.ExpandMore,
                    contentDescription = if (expanded) {
                        stringResource(R.string.show_less)
                    } else {
                        stringResource(R.string.show_more)
                    }
                )

            }

            if (expanded) {
                Column {
                    Text(
                        text = ("Level: ${player.level}"),
                        modifier = Modifier.padding(8.dp)
                    )
                    Text(
                        text = ("Ping: ${player.ping}"),
                        modifier = Modifier.padding(8.dp)
                    )
                    Text(
                        text = ("IP: ${player.ip}"),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun PlayerCardPreview() {
    PlayerCard(
        Player(
            name = "Kyledoober"
        )
    )
}

@Preview(showBackground = true, heightDp = 400, widthDp = 200)
@Composable
fun PlayersPreview() {
    PlayersContent(
        uiState = PlayersUiState(
            playersModel = PlayersModel(
                players = PlayersModel.createDummyData(10)
            )
        )
    )
}

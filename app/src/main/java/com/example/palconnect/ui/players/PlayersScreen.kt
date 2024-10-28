package com.example.palconnect.ui.players

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
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
    LazyColumn(
        modifier = Modifier
    ) {
        items(items = uiState.playersModel.players) { player ->
            PlayerCard(player)
        }
    }

}

@Composable
fun PlayerCard(
    player: Player,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = player.name,
            modifier = Modifier.padding(16.dp)
        )
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

package com.example.palconnect.ui.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.palconnect.PalConnectApp
import com.example.palconnect.R
import com.example.palconnect.conditional
import com.example.palconnect.services.PalDataStore
import com.example.palconnect.ui.theme.PalConnectTheme
import com.example.palconnect.viewmodels.ConfigUiState
import com.example.palconnect.viewmodels.ConfigViewModel
import com.example.palconnect.viewmodels.viewModelFactory
import kotlinx.coroutines.flow.first

@Composable
fun ConfigScreen(
    modifier: Modifier = Modifier,
    topBarTitle: MutableState<String>,
    viewModel: ConfigViewModel = viewModel(
        factory = ConfigViewModel.Factory
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    topBarTitle.value = stringResource(R.string.config)
    val context = LocalContext.current


    ConfigContent(
        modifier = modifier,
        uiState = uiState,
        onIpTextChange = { text ->
            viewModel.ipTextChanged(text)
        },
        onPasswordTextChange = { text ->
            viewModel.passwordTextChanged(text)
        },
        onIpSubmit = {
            focusRequester.requestFocus()
        },
        onPasswordSubmit = {
            viewModel.submitted()
        },
        onButtonSubmit = { viewModel.submitted() },
        passwordFocusRequester = focusRequester,
    )
}

@Composable
fun ConfigContent(
    modifier: Modifier = Modifier,
    uiState: ConfigUiState,
    onIpTextChange: (String) -> Unit = {},
    onPasswordTextChange: (String) -> Unit = {},
    onIpSubmit: () -> Unit = {},
    onPasswordSubmit: () -> Unit = {},
    onButtonSubmit: () -> Unit = {},
    passwordFocusRequester: FocusRequester? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 64.dp)
    ) {
        if(uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
            )
        } else {
            Text(
                text = "Enter Server Info",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            IpTextField(
                text = uiState.ipField,
                onTextChange = onIpTextChange,
                onSubmit = onIpSubmit
            )
            Spacer(Modifier.height(4.dp))
            PasswordTextField(
                text = uiState.passwordField,
                onTextChange = onPasswordTextChange,
                onSubmit = onPasswordSubmit,
                modifier = Modifier.conditional(passwordFocusRequester != null) {
                    focusRequester(passwordFocusRequester!!)
                }
            )
            ElevatedButton(
                enabled = uiState.canSubmit,
                onClick = onButtonSubmit,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text("Submit")
            }
            Text(text = uiState.infoModel.servername)
            Text(text = uiState.message)
        }
    }
}

@Composable
fun IpTextField(
    text: String,
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit = {text -> },
    onSubmit: () -> Unit = {},
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = text,
        placeholder = {
            Text("IP")
        },
        onValueChange = onTextChange,
        textStyle = MaterialTheme.typography.titleLarge,
        singleLine = true,
        keyboardActions = KeyboardActions(onDone = { onSubmit() })
    )
}

@Composable
fun PasswordTextField(
    text: String,
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit = {text -> },
    onSubmit: () -> Unit = {},
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = text,
        placeholder = {
            Text("Password")
        },
        onValueChange = onTextChange,
        visualTransformation = PasswordVisualTransformation(),
        textStyle = MaterialTheme.typography.titleLarge,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        keyboardActions = KeyboardActions(onDone = { onSubmit() })
    )
}

@Preview(showBackground = true)
@Composable
fun ConfigPreview() {
    PalConnectTheme {
        ConfigContent(uiState = ConfigUiState())
    }
}
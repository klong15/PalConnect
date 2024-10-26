package com.example.palconnect.config

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.palconnect.PalApp
import com.example.palconnect.ui.theme.PalConnectTheme
import com.example.palconnect.viewmodels.MainViewModel
import com.example.palconnect.viewmodels.viewModelFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun ConfigContent(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(
        factory = viewModelFactory {
            MainViewModel(PalApp.palApiModule.palApiService)
        }
    ),
) {
//    val modelState by viewModel.model.observeAsState()
    val configUiState by viewModel.configUiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize().padding(horizontal = 64.dp)
    ) {
        if(configUiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
            )
        } else {
            Text(
                text = "Enter Server Info",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            val focusRequester = remember { FocusRequester() }
            IpTextField(
                text = configUiState.ipField,
                onTextChange = { text ->
                    viewModel.ipTextChanged(text)
                },
                onSubmit = {
                    focusRequester.requestFocus()
                }
            )
            Spacer(Modifier.height(4.dp))
            PasswordTextField(
                text = configUiState.passworldField,
                onTextChange = { text ->
                    viewModel.passwordTextChanged(text)
                },
                onSubmit = {
                    viewModel.submitted()
                },
                modifier = Modifier.focusRequester(focusRequester)
            )
            ElevatedButton(
                enabled = configUiState.canSubmit,
                onClick = { viewModel.submitted() },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text("Submit")
            }
            Text(text = configUiState.infoModel.servername)
            Text(text = configUiState.message)
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
        ConfigContent()
    }
}
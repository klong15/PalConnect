package com.example.palconnect.ui.theme.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.palconnect.ui.theme.PalConnectTheme
import com.example.palconnect.viewmodels.MainViewModel

@Composable
fun ConfigContent(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(),
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize().padding(horizontal = 64.dp)
    ){
        Text(
            text = "Enter Server Info",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        IpTextField(viewModel.ip) { text ->
            viewModel.ipTextChanged(text)
        }
        Spacer(Modifier.height(4.dp))
        PasswordTextField(viewModel.password) { text ->
            viewModel.passwordTextChanged(text)
        }
        ElevatedButton(
            enabled = viewModel.canSubmit,
            onClick = { viewModel.submitted() },
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text("Submit")
        }
    }
}

@Composable
fun IpTextField(
    text: String,
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit = {text -> },
) {
    TextField(
        value = text,
        placeholder = {
            Text("IP")
        },
        onValueChange = onTextChange,
        textStyle = MaterialTheme.typography.titleLarge,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun PasswordTextField(
    text: String,
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit = {text -> },
) {
    TextField(
        value = text,
        placeholder = {
            Text("Password")
        },
        onValueChange = onTextChange,
        visualTransformation = PasswordVisualTransformation(),
        textStyle = MaterialTheme.typography.titleLarge,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun ConfigPreview() {
    PalConnectTheme {
        ConfigContent()
    }
}
package com.example.palconnect.ui

import androidx.compose.material.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.palconnect.NavBarAction
import com.example.palconnect.viewmodels.LoginViewModel
import com.example.palconnect.viewmodels.OverviewViewModel
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    topBarTitle: MutableState<String>,
    windowSize: WindowSizeClass,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: LoginViewModel = viewModel(
        factory = LoginViewModel .Factory
    ),
) {

    Text("Hello Login")
}
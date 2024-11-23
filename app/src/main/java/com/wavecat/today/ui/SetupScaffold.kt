package com.wavecat.today.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.wavecat.today.MainViewModel
import com.wavecat.today.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScaffold(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: MainViewModel,
    isWidgetConfiguration: Boolean,
    onSuccessConfiguration: () -> Unit,
) {
    val successString = stringResource(R.string.success)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            if (isWidgetConfiguration)
                ExtendedFloatingActionButton(
                    onClick = { onSuccessConfiguration() },
                    icon = { Icon(Icons.Filled.Check, stringResource(R.string.apply)) },
                    text = { Text(stringResource(R.string.apply)) },
                )
            else
                ExtendedFloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(successString)
                        }

                        viewModel.generate()
                    },
                    icon = { Icon(Icons.Filled.PlayArrow, stringResource(R.string.generate)) },
                    text = { Text(stringResource(R.string.generate)) },
                )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val apiUrl by viewModel.apiUrl.collectAsState()
            val apiKey by viewModel.apiKey.collectAsState()
            val contextSize by viewModel.contextSize.collectAsState()
            val prompt by viewModel.prompt.collectAsState()
            val model by viewModel.model.collectAsState()
            val interval by viewModel.interval.collectAsState()

            SetupScreen(
                apiUrl = apiUrl,
                setApiUrl = viewModel::setApiUrl,
                apiKey = apiKey,
                setApiKey = viewModel::setApiKey,
                model = model,
                setModel = viewModel::setModel,
                prompt = prompt,
                setPrompt = viewModel::setPrompt,
                interval = interval,
                setInterval = viewModel::setInterval,
                contextSize = contextSize,
                setContextSize = viewModel::setContextSize
            )
        }
    }
}
package com.wavecat.today.setup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
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
    viewModel: MainViewModel
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
            ExtendedFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(successString)
                    }

                    viewModel.apply()
                },
                icon = { Icon(Icons.Filled.Check, stringResource(R.string.apply)) },
                text = { Text(stringResource(R.string.apply)) },
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
            val notificationHours by viewModel.notificationHours.collectAsState()

            SetupScreen(
                apiUrl = apiUrl,
                setApiUrl = viewModel::setApiUrl,
                apiKey = apiKey,
                setApiKey = viewModel::setApiKey,
                model = model,
                setModel = viewModel::setModel,
                prompt = prompt,
                setPrompt = viewModel::setPrompt,
                notificationHours = notificationHours,
                setNotificationHours = viewModel::setNotificationHours,
                contextSize = contextSize,
                setContextSize = viewModel::setContextSize
            )
        }
    }
}
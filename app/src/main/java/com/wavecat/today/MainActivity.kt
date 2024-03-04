package com.wavecat.today

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkManager
import com.wavecat.today.setup.SetupScaffold
import com.wavecat.today.ui.theme.TodayTheme


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
                .launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        if (!NotificationManagerCompat.getEnabledListenerPackages(this)
                .contains(packageName)
        ) {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
        }

        setContent {
            TodayTheme {
                val snackbarHostState = remember { SnackbarHostState() }

                val viewModel = viewModel {
                    MainViewModel(
                        DataRepository(application),
                        WorkManager.getInstance(application)
                    )
                }

                LifecycleResumeEffect(Unit) {
                    viewModel.onResume()
                    onPauseOrDispose {
                        viewModel.onPause()
                    }
                }

                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetupScaffold(
                        coroutineScope = rememberCoroutineScope(),
                        snackbarHostState = snackbarHostState,
                        scrollBehavior = scrollBehavior,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}


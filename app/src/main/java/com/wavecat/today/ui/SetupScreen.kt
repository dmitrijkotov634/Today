package com.wavecat.today.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wavecat.today.Constant
import com.wavecat.today.R
import com.wavecat.today.ui.theme.TodayTheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SetupScreen(
    apiUrl: String,
    setApiUrl: (String) -> Unit,
    apiKey: String,
    setApiKey: (String) -> Unit,
    model: String,
    setModel: (String) -> Unit,
    prompt: String,
    setPrompt: (String) -> Unit,
    interval: Duration,
    setInterval: (Duration) -> Unit,
    contextSize: Int,
    setContextSize: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val isTestEndpoint = apiUrl == Constant.TEST_ENDPOINT

        OutlinedTextField(
            value = apiUrl,
            onValueChange = { setApiUrl(it) },
            label = { Text(stringResource(R.string.api_url)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isTestEndpoint
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row {
            SuggestionChip(
                onClick = { setApiUrl(Constant.TEST_ENDPOINT) },
                label = { Text(stringResource(R.string.free_testing)) }
            )

            Spacer(modifier = Modifier.width(8.dp))

            SuggestionChip(
                onClick = { setApiUrl(Constant.OPENAI_ENDPOINT) },
                label = { Text("OpenAI") }
            )

            Spacer(modifier = Modifier.width(8.dp))

            SuggestionChip(
                onClick = {
                    setApiUrl(Constant.GROQ_ENDPOINT)
                    setApiKey(Constant.DEFAULT_GROQ_KEY)
                    setModel(Constant.DEFAULT_GROQ_MODEL)
                },
                label = { Text("Groq Cloud") }
            )
        }

        AnimatedVisibility(isTestEndpoint) {
            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = stringResource(id = R.string.testing_warning),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(14.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = apiKey,
            onValueChange = { setApiKey(it) },
            label = { Text(stringResource(R.string.api_key)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            enabled = !isTestEndpoint
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = model,
            onValueChange = { setModel(it) },
            label = { Text(stringResource(R.string.model)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isTestEndpoint
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(stringResource(R.string.context_size), style = MaterialTheme.typography.titleSmall)

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = contextSize.toString())

            Spacer(modifier = Modifier.width(16.dp))

            Slider(
                value = contextSize.toFloat(),
                onValueChange = { setContextSize(it.toInt()) },
                valueRange = 500f..9000f,
                enabled = !isTestEndpoint,
                steps = 16
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = prompt,
            onValueChange = { setPrompt(it) },
            label = { Text(stringResource(R.string.prompt)) },
            singleLine = false,
            minLines = 12,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = ExpressionTransformation(
                MaterialTheme.colorScheme.primary,
                variables = Constant.variables
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            for (variable in Constant.variables) {
                SuggestionChip(
                    onClick = { setPrompt("$prompt $variable") },
                    label = { Text(variable) }
                )

                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(stringResource(R.string.periodicity), style = MaterialTheme.typography.titleSmall)

        Spacer(modifier = Modifier.height(10.dp))

        IntervalDropdown(
            modifier = Modifier.width(220.dp),
            interval = interval,
            startInterval = if (isTestEndpoint) 1.hours else 0.milliseconds,
            onIntervalChanged = {
                setInterval(it)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun SetupScreenPreview() {
    TodayTheme {
        SetupScreen(
            apiUrl = Constant.OPENAI_ENDPOINT,
            setApiUrl = {},
            apiKey = "",
            setApiKey = {},
            model = Constant.DEFAULT_MODEL,
            setModel = {},
            prompt = "",
            setPrompt = {},
            interval = 1.hours,
            setInterval = {},
            contextSize = 2000,
            setContextSize = {}
        )
    }
}

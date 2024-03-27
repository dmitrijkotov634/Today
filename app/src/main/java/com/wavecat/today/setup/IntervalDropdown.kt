package com.wavecat.today.setup

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.wavecat.today.R
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntervalDropdown(
    interval: Duration,
    onIntervalChanged: (Duration) -> Unit,
    startInterval: Duration,
    modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = intervalValue(duration = interval),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .then(modifier)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            for (it in 10..60 step 15) {
                DropdownMenuItem(
                    onClick = {
                        onIntervalChanged(it.minutes)
                        expanded = false
                    },
                    text = { Text(intervalValue(duration = it.minutes)) },
                    enabled = startInterval <= it.minutes
                )
            }

            for (it in 1..13 step 2) {
                DropdownMenuItem(
                    onClick = {
                        onIntervalChanged(it.hours)
                        expanded = false
                    },
                    text = { Text(intervalValue(duration = it.hours)) },
                    enabled = startInterval <= it.hours
                )
            }
        }
    }
}

@Composable
fun intervalValue(duration: Duration) =
    if (duration >= 1.hours)
        stringResource(
            when (duration) {
                1.hours -> R.string.every_hour
                in 5.hours..24.hours -> R.string.every_hours_5h
                else -> R.string.every_hours
            }, duration.inWholeHours
        )
    else
        stringResource(
            R.string.every_minutes,
            duration.inWholeMinutes
        )
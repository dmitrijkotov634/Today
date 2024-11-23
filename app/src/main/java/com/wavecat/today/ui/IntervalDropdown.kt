package com.wavecat.today.ui

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    modifier: Modifier,
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
                .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                .then(modifier)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            for (minutes in 10..60 step 15) {
                DropdownMenuItem(
                    onClick = {
                        onIntervalChanged(minutes.minutes)
                        expanded = false
                    },
                    text = { Text(intervalValue(duration = minutes.minutes)) },
                    enabled = startInterval <= minutes.minutes
                )
            }

            for (hours in 1..13 step 2) {
                DropdownMenuItem(
                    onClick = {
                        onIntervalChanged(hours.hours)
                        expanded = false
                    },
                    text = { Text(intervalValue(duration = hours.hours)) },
                    enabled = startInterval <= hours.hours
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
package com.wavecat.today.setup

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.wavecat.today.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoursDropDown(hours: Int, onHoursChanged: (Int) -> Unit, modifier: Modifier) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = hoursValue(hours = hours),
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
            (1..13 step 2).forEach {
                DropdownMenuItem(onClick = {
                    onHoursChanged(it)
                    expanded = false
                }, text = {
                    Text(hoursValue(hours = it))
                })
            }
        }
    }
}

@Composable
fun hoursValue(hours: Int) = stringResource(
    when (hours) {
        1 -> R.string.every_hour
        in 5..24 -> R.string.every_hours_5h
        else -> R.string.every_hours
    }, hours
)
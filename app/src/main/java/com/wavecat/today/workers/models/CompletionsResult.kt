package com.wavecat.today.workers.models

import kotlinx.serialization.Serializable

@Serializable
data class CompletionsResult(
    val choices: List<CompletionsChoices>? = null,
    val error: Error? = null
)
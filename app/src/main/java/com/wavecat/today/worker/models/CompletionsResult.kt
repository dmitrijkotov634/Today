package com.wavecat.today.worker.models

import kotlinx.serialization.Serializable

@Serializable
data class CompletionsResult(
    val choices: List<CompletionsChoices>? = null,
    val error: Error? = null
)
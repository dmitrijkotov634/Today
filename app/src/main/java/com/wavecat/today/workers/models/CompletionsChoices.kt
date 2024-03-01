package com.wavecat.today.workers.models

import kotlinx.serialization.Serializable

@Serializable
data class CompletionsChoices(
    val message: Message
)
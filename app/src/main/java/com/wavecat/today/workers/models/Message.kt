package com.wavecat.today.workers.models

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val role: String,
    @Required
    var content: String? = "",
) {
    companion object {
        const val USER = "user"
        const val ASSISTANT = "assistant"
        const val SYSTEM = "system"
    }
}
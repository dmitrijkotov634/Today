package com.wavecat.today.workers.models

import kotlinx.serialization.Serializable

@Serializable
data class Error(
    val message: String,
    val type: String? = null
)
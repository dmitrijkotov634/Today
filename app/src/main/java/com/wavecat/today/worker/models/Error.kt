package com.wavecat.today.worker.models

import kotlinx.serialization.Serializable

@Serializable
data class Error(
    val message: String,
    val type: String? = null
)
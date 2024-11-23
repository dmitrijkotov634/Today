package com.wavecat.today.worker.models

import kotlinx.serialization.Serializable

@Serializable
data class Error(
    override val message: String,
    val type: String? = null,
) : Throwable(message)
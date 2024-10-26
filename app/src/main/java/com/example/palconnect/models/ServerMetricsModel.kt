package com.example.palconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class ServerMetricsModel(
    val currentplayernum: Int = 0,
    val serverfps: Int = 0,
    val serverframetime: Float = 0f,
    val maxplayernum: Int = 0,
    val uptime: Long = 0L,
)
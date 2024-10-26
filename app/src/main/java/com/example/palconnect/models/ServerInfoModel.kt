package com.example.palconnect.models

import kotlinx.serialization.Serializable


@Serializable
data class ServerInfoModel(
    val version: String = "",
    val servername: String = "",
    val description: String = "",
)
package com.example.palconnect.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class PlayersModel(
    val players: Array<Player> = Array<Player>(
        size = 0,
        init = { index -> Player() }
    )
)

@Serializable
data class Player(
    val name: String = "",
    val accountName: String = "",
    val playerId:String = "",
    val userId:String = "",
    val ip:String = "",
    val ping:Double = 0.0,
    @SerialName("location_x")
    val locationX:Double = 0.0,
    @SerialName("location_y")
    val locationY:Double = 0.0,
    val level:Int = 0
)
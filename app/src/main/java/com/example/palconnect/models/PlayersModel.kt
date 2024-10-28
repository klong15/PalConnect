package com.example.palconnect.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class PlayersModel(
    val players: Array<Player> = PlayersModel.createDummyData(0)
) {
    companion object {
        fun createDummyData(size: Int): Array<Player> {
            return Array<Player>(
                size = size,
                init = { index ->
                    Player(
                        name = "Player${index}",
                        playerId = "fake_player_${index}"
                    )
                }
            )
        }
    }
}

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
package com.example.apz.dto.responses

import kotlinx.serialization.json.Json

@kotlinx.serialization.Serializable
data class PlayerResponse(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val avatar: String,
    val birthDate: String,
    val games: List<GameFull>,
)

@kotlinx.serialization.Serializable
data class GameFull(
    val id: Int,
    val gameStartDate: String,
    val gameEndDate: String,
    val description: String,
    val heartBeats: List<HeartBeat>,
    val playerId: Int,
    val isLastHeartBeatOk: Boolean,
    val sensorId: String,
    val isPlayerAbsent: Boolean

)

@kotlinx.serialization.Serializable
data class HeartBeat(
    val heartBeatDate: String,
    val value: Double
)

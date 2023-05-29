package com.example.apz.dto.responses

@kotlinx.serialization.Serializable
data class GameShort(
    val id: Int,
    val gameStartDate: String,
    val gameEndDate: String,
    val avgHeartBeat: Double,
)
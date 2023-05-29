package com.example.apz.dto.responses

@kotlinx.serialization.Serializable
data class GameWithAnalysis(
    val id: Int,
    val gameStartDate: String,
    val isPlayerAbsent: Boolean,
    val gameEndDate: String,
    val description: String,
    val heartBeats: List<HeartBeat>,
    val playerId: Int,
    val analysis: GameAnalysis?,
    val sensorId: String
)

@kotlinx.serialization.Serializable
data class GameAnalysis(
    val isRangeGood: Boolean,
    val isAverageHigher: Boolean,
    val isAverageLower: Boolean,
    val timesLowerMinimumHeartBeat: Int,
    val timesMoreMaxHeartBeat: Int,
)
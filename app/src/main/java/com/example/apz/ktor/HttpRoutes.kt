package com.example.apz.ktor

object HttpRoutes {
    private const val BASE_URL = "http://10.0.2.2:5052"
    const val LOGIN = "$BASE_URL/api/Auth/player/login"
    const val GAMES = "$BASE_URL/api/Game/games/player" // Dynamic path segment
    const val GAME_DETAILS = "$BASE_URL/api/Game/game/{id}" // Dynamic path segment
    const val PLAYER = "$BASE_URL/api/Team/player"

    fun getGameDetailsUrl(id: String): String {
        return GAME_DETAILS.replace("{id}", id)
    }
}
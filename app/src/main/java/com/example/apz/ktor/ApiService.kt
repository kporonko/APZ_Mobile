package com.example.apz.ktor

import android.content.Context
import com.example.apz.dto.responses.*
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*

interface ApiService {

    suspend fun getPlayer(token: String): PlayerResponse
    suspend fun login(login: String, password: String, context: Context): LoginResponse
    suspend fun getGames(token: String): List<GameShort>
    suspend fun getGame(id: Int, token: String): GameWithAnalysis?
    suspend fun getTemp(id: String, key: String, start: String, end: String): TempResponse

    companion object {
        fun create(): ApiService {
            return ApiServiceImpl(
                client = HttpClient(Android) {
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                    install(JsonFeature) {
                        serializer = KotlinxSerializer()
                    }
                }
            )
        }
    }
}
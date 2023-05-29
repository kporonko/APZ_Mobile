package com.example.apz.ktor

import android.content.Context
import android.util.Log
import com.example.apz.dto.requests.LoginRequest
import com.example.apz.dto.responses.*
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.utils.EmptyContent.headers
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*

class ApiServiceImpl(
    private val client: HttpClient
) : ApiService {

    override suspend fun getPlayer(token: String): PlayerResponse {
        return try {
            client.get {
                url(HttpRoutes.PLAYER)
                headers {
                    append("Authorization", "Bearer $token")
                }
            }
        } catch(e: RedirectResponseException) {
            // 3xx - responses
            println("Error: ${e.response.status.description}")
            PlayerResponse(-1, "", "", "", "", emptyList())
        } catch(e: ClientRequestException) {
            // 4xx - responses
            println("Error: ${e.response.status.description}")
            PlayerResponse(-1, "", "", "", "", emptyList())
        } catch(e: ServerResponseException) {
            // 5xx - responses
            println("Error: ${e.response.status.description}")
            PlayerResponse(-1, "", "", "", "", emptyList())
        } catch(e: Exception) {
            println("Error: ${e.message}")
            PlayerResponse(-1, "", "", "", "", emptyList())
        }
    }

    override suspend fun login(login: String, password: String, context: Context): LoginResponse {

        return try {
            val response = client.post<LoginResponse> {
                url(HttpRoutes.LOGIN)
                body = LoginRequest(login, password)
                contentType(ContentType.Application.Json)
            }
            val token = response.token
            SharedPreferencesHelper.saveAuthToken(context, token) // Save the token to shared preferences
            response
        } catch (e: Exception) {
            println("Error Login: ${e.message}")
            LoginResponse(token = "")
        }
    }
    override suspend fun getGames(token: String): List<GameShort> {
        return try {
            client.get {
                url(HttpRoutes.GAMES)
                headers {
                    append("Authorization", "Bearer $token")
                }
            }
        } catch(e: RedirectResponseException) {
            // 3xx - responses
            println("Error: ${e.response.status.description}")
            emptyList<GameShort>()
        } catch(e: ClientRequestException) {
            // 4xx - responses
            println("Error: ${e.response.status.description}")
            emptyList<GameShort>()
        } catch(e: ServerResponseException) {
            // 5xx - responses
            println("Error: ${e.response.status.description}")
            emptyList<GameShort>()
        } catch(e: Exception) {
            println("Error: ${e.message}")
            emptyList<GameShort>()
        }
    }

    override suspend fun getGame(id: Int, token: String): GameWithAnalysis? {
        return try {
            client.get {
                url(HttpRoutes.getGameDetailsUrl(id.toString()))
                headers {
                    append("Authorization", "Bearer $token")
                }
            }
        } catch(e: RedirectResponseException) {
            // 3xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch(e: ClientRequestException) {
            // 4xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch(e: ServerResponseException) {
            // 5xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch(e: Exception) {
            println("Error: ${e.message}")
            null
        }
    }

    override suspend fun getTemp(id: String, key: String, start: String, end: String): TempResponse {
        Log.d("ApiTempCall", start)
        Log.d("ApiTempCall", end)

        return try {
            client.get{url("https://api.thingspeak.com/channels/$id/feeds.json?api_key=$key&start=$start&end=$end")}
        }catch(e: Exception) {
            println("Error: ${e.message}")
            TempResponse(feeds = emptyList(), channel = ChannelResponse(id = 0, name = "", description = "", latitude= "", longitude = "", field1 = "", field2 = "", created_at = "", updated_at = "", last_entry_id = 0))
        }
    }

}
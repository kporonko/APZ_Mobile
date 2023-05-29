package com.example.apz.components

import android.util.Base64
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64.decode
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.apz.dto.responses.GameFull
import com.example.apz.dto.responses.PlayerResponse
import com.example.apz.dto.responses.TempResponseItem
import com.example.apz.ktor.ApiService
import com.example.apz.ktor.SharedPreferencesHelper.getAuthToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.apz.R
import coil.compose.rememberImagePainter
import com.example.apz.Routes



@Composable
fun MainPage(navController: NavHostController, activity: ComponentActivity, token: String) {
    val isLoading: MutableState<Boolean> = remember { mutableStateOf(false) }
    val player = remember { mutableStateOf<PlayerResponse?>(null) }
    var client = ApiService.create()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val jwttoken = getAuthToken(context);

    LaunchedEffect(token) {
        val client = ApiService.create()
        isLoading.value = true

        try {
            if (!jwttoken.isNullOrEmpty()){
                val playerResponse = client.getPlayer(jwttoken)
                player.value = playerResponse
                Log.d("Hello", jwttoken)
                Log.d("Hello", playerResponse.firstName)
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        isLoading.value = false
    }

    if (player.value != null) {
        GamesList(player = player.value!!, client, coroutineScope, context, navController)
    }}

@Composable
fun GamesList(player: PlayerResponse, client: ApiService, scope: CoroutineScope, context: Context, navController: NavHostController){
    Log.d("Avatar", player.avatar)

    val prefixPng = "data:image/png;base64,"
    val prefixJpeg = "data:image/jpeg;base64,"
    val prefixGif = "data:image/gif;base64,"
    val prefixBmp = "data:image/bmp;base64,"
    val prefixWebp = "data:image/webp;base64,"

    val imageData: String = when {
        player.avatar.startsWith(prefixPng) -> player.avatar.removePrefix(prefixPng)
        player.avatar.startsWith(prefixJpeg) -> player.avatar.removePrefix(prefixJpeg)
        player.avatar.startsWith(prefixGif) -> player.avatar.removePrefix(prefixGif)
        player.avatar.startsWith(prefixBmp) -> player.avatar.removePrefix(prefixBmp)
        player.avatar.startsWith(prefixWebp) -> player.avatar.removePrefix(prefixWebp)

        else -> player.avatar
    }

    val decodedByte = Base64.decode(imageData, Base64.DEFAULT)
    val bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
    val img = bitmap.asImageBitmap()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            Text(
                text = "Player",
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 34.sp,
                modifier = Modifier.padding(10.dp, 15.dp)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp)
                ) {
                    Image(
                        bitmap = img,
                        contentDescription = "Base64 Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Text(text = "First Name: ${player.firstName}")
                    Text(text = "Last Name: ${player.lastName}")
                    Text(text = "Birth Date: ${convertIsoStringToLocaleDate(player.birthDate, Locale.getDefault())}")
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            Text(
                text = "Games",
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.padding(10.dp, 15.dp)
            )
        }
        player.games.forEach { game ->
            GameCard(game = game, client, scope, context, navController)
        }
    }
}


fun convertIsoStringToLocaleDate(isoString: String, locale: Locale): String? {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", locale)
    return try {
        val date = format.parse(isoString)
        val localizedFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, locale)
        localizedFormat.format(date)
    } catch (e: Exception) {
        null
    }
}

fun convertIsoStringToLocaleDateTime(isoString: String, locale: Locale): String? {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", locale)
    return try {
        val date = format.parse(isoString)
        val localizedFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.SHORT, locale)
        localizedFormat.format(date)
    } catch (e: Exception) {
        null
    }
}

fun convertIsoStringToLocaleTime(isoString: String, locale: Locale): String? {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", locale)
    return try {
        val date = format.parse(isoString)
        val localizedFormat = SimpleDateFormat("HH:mm:ss", locale)
        localizedFormat.format(date)
    } catch (e: Exception) {
        null
    }
}

fun convertIsoStringToLocaleTimeWithZ(isoString: String, locale: Locale): String? {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", locale)
    format.timeZone = TimeZone.getTimeZone("UTC")
    return try {
        val date = format.parse(isoString)
        val localizedFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, locale)
        localizedFormat.format(date)
    } catch (e: Exception) {
        null
    }
}

@Composable
fun GameCard(game: GameFull, client: ApiService, coroutineScope: CoroutineScope, context: Context, navController: NavHostController) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(16.dp)
            .clickable {
                navController.navigate(Routes.GamePage.passId(game.id))
            }
        ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = game.id.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = "Start Date: ${convertIsoStringToLocaleDateTime(game.gameStartDate, Locale.getDefault())}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "End Date: ${convertIsoStringToLocaleDateTime(game.gameEndDate,Locale.getDefault())}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
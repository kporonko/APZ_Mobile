package com.example.apz.components

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.ScrollView
import androidx.activity.ComponentActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.apz.dto.responses.GameWithAnalysis
import com.example.apz.dto.responses.HeartBeat
import com.example.apz.dto.responses.PlayerResponse
import com.example.apz.ktor.ApiService
import com.example.apz.ktor.SharedPreferencesHelper
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.apz.dto.responses.TempResponse
import com.himanshoe.charty.common.axis.AxisConfig
import com.himanshoe.charty.common.dimens.ChartDimens
import com.himanshoe.charty.line.LineChart
import com.himanshoe.charty.line.model.LineData
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


const val API_KEY = "AV9VRNL2O9T3RHI5"

@Composable
fun GamePage(navController: NavHostController, activity: ComponentActivity, gameId: Int) {
    val isLoading: MutableState<Boolean> = remember { mutableStateOf(false) }
    val game = remember { mutableStateOf<GameWithAnalysis?>(null) }
    val temp = remember { mutableStateOf<TempResponse?>(null) }
    var client = ApiService.create()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val jwttoken = SharedPreferencesHelper.getAuthToken(context);

    LaunchedEffect(gameId) {
        val client = ApiService.create()
        isLoading.value = true

        try {
            if (!jwttoken.isNullOrEmpty()){
                val gameResponse = client.getGame(gameId, jwttoken)
                game.value = gameResponse
                Log.d("Hello", jwttoken)
                Log.d("Hello", gameResponse?.id.toString())
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }

        isLoading.value = false
    }
    if (game.value != null) {
        LaunchedEffect(game.value) {
            try {
                val tempResponse = client.getTemp(game.value!!.sensorId, API_KEY, subtractHoursFromIsoString(game.value!!.gameStartDate, 3), subtractHoursFromIsoString(game.value!!.gameEndDate, 3))
                Log.d("Temp", game.value!!.gameStartDate)
                Log.d("Temp", subtractHoursFromIsoString(game.value!!.gameStartDate, 3))
                Log.d("Temp", game.value!!.gameEndDate)
                Log.d("Temp", subtractHoursFromIsoString(game.value!!.gameEndDate, 3))
                temp.value = tempResponse;
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
        if (temp.value != null){
            Game(game = game.value!!, temp = temp.value!!, client, coroutineScope, context)
        }
    }

//    if (game.value != null) {
//         Game(game = game.value!!, client, coroutineScope, context)
//    }
}

fun subtractHoursFromIsoString(isoString: String, hours: Long): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
    val dateTime = LocalDateTime.parse(isoString, formatter)
    val updatedDateTime = dateTime.minusHours(hours)
    return updatedDateTime.format(formatter)
}

@Composable
fun Game(game: GameWithAnalysis, temp: TempResponse, client: ApiService, scope: CoroutineScope, context: Context){
    val state = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(state)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            Text(
                text = "Game Info",
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 34.sp,
                modifier = Modifier.padding(10.dp, 15.dp)
            )
        }

        Spacer(modifier = Modifier.size(30.dp))

        Text(
            text = "ID: ${game.id}",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Start Date: ${convertIsoStringToLocaleDateTime(game.gameStartDate, Locale.getDefault())}",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "End Date: ${convertIsoStringToLocaleDateTime(game.gameEndDate, Locale.getDefault())}",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        game.analysis?.let { analysis ->
            Spacer(modifier = Modifier.size(30.dp))
            Text(
                text = "Analysis",
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontSize = 26.sp,
            )
            Spacer(modifier = Modifier.size(30.dp))

            AnalysisItem(
                label = "Heartbeat range",
                value = analysis.isRangeGood,
                recommendation = "Engage in regular physical activity and exercise outside of gaming sessions. Regular exercise helps improve cardiovascular health and can contribute to a more stable heartbeat during gaming sessions."
            )
            Spacer(modifier = Modifier.size(5.dp))

            AnalysisItem(
                label = "Average not low",
                value = !analysis.isAverageLower,
                recommendation = "Review average heart rate is bad ${analysis.isAverageLower}"
            )
            Spacer(modifier = Modifier.size(5.dp))

            AnalysisItem(
                label = "Average not high",
                value = !analysis.isAverageHigher,
                recommendation = "Review average heart rate is bad ${analysis.isAverageHigher}"
            )
            Spacer(modifier = Modifier.size(5.dp))

            AnalysisItem(
                label = "Lower Heart Beat",
                value = analysis.timesLowerMinimumHeartBeat <= 0,
                recommendation = "You had ${analysis.timesLowerMinimumHeartBeat} heartbeats below normal"
            )
            Spacer(modifier = Modifier.size(5.dp))

            AnalysisItem(
                label = "Higher Heart Beat",
                value = analysis.timesMoreMaxHeartBeat <= 0,
                recommendation = "You had ${analysis.timesMoreMaxHeartBeat} heartbeats above normal"
            )
        }

        val colors = listOf(Color.Red, Color.Green, Color.Blue)

        val lineData = game.heartBeats.map { heartBeat ->
            LineData(
                xValue = convertIsoStringToLocaleTime(heartBeat.heartBeatDate, Locale.getDefault())!!,
                yValue = heartBeat.value.toFloat()
            )
        }

        val field1LineData = temp.feeds.map { data ->
            LineData(
                xValue = convertIsoStringToLocaleTimeWithZ(data.created_at, Locale.getDefault())!!,
                yValue = data.field1.toFloat()
            )
        }

        val field2LineData = temp.feeds.map { data ->
            LineData(
                xValue = convertIsoStringToLocaleTimeWithZ(data.created_at, Locale.getDefault())!!,
                yValue = data.field2.toFloat()
            )
        }

        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "Heartbeat chart",
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontSize = 26.sp,
        )
        Spacer(modifier = Modifier.size(10.dp))

        Card(modifier = Modifier.border(2.dp, Color.Black, RectangleShape)) {
            LineChart(
                lineData = lineData,  // list of LineData
                colors = colors,// colors
                modifier = Modifier
                    .fillMaxSize()
                    .height(200.dp)
                    .padding(30.dp)
            )
        }

        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "Temperature chart",
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontSize = 26.sp,
        )
        Spacer(modifier = Modifier.size(10.dp))


        Card(modifier = Modifier.border(2.dp, Color.Black, RectangleShape)) {
            LineChart(
                lineData = field1LineData,  // list of LineData
                colors = colors,// colors
                modifier = Modifier
                    .fillMaxSize()
                    .height(200.dp)
                    .padding(30.dp)
            )
        }

        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "Humidity chart",
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontSize = 26.sp,
        )
        Spacer(modifier = Modifier.size(10.dp))


        Card(modifier = Modifier.border(2.dp, Color.Black, RectangleShape)) {
            LineChart(
                lineData = field2LineData,  // list of LineData
                colors = colors,// colors
                modifier = Modifier
                    .fillMaxSize()
                    .height(200.dp)
                    .padding(30.dp)
            )
        }
    }
}


@Composable
fun AnalysisItem(
    label: String,
    value: Boolean,
    recommendation: String
) {
    val color = if (value) Color.Green else Color.Red

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color)
        )

        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.size(10.dp))
        if (!value){
            Text(
                text = recommendation,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

    }
}

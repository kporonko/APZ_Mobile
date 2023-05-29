package com.example.apz.components

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.apz.Routes

@Composable
fun Start(activity: ComponentActivity){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Login.route) {
        composable(Routes.Login.route) {
            LoginPage(navController = navController, activity = activity)
        }
        composable(
            route = Routes.MainPage.route,
            arguments = listOf(navArgument("token"){
                type = NavType.StringType
            })
        ) {
            Log.d("Args", it.arguments?.getInt("token").toString())
            MainPage(navController = navController, activity = activity, it.arguments?.getString("token")!!.toString())
        }
        composable(
            route = Routes.GamePage.route,
            arguments = listOf(navArgument("gameId"){
                type = NavType.IntType
            })
        ) {
            Log.d("Args", it.arguments?.getInt("gameId").toString())
            GamePage(navController = navController, activity = activity, it.arguments?.getInt("gameId")!!.toInt())
        }
    }
}
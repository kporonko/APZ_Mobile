package com.example.apz

sealed class Routes(val route: String) {
    object Login : Routes("Login")
    object MainPage : Routes("Main/{token}"){
        fun passId(token: String): String{
            return "Main/${token}"
        }
    }
    object GamePage : Routes("GamePage/{gameId}"){
        fun passId(gameId: Int): String{
            return "GamePage/${gameId}"
        }
    }
}
package com.example.wifimapping.navigation

enum class  Screens{
    HomeScreen,
    ChooseWifiScreen,
    PreviewGridScreen;
    companion object {
        fun fromRoute(route: String?): Screens
                = when (route?.substringBefore("/")){
            HomeScreen.name -> HomeScreen
            ChooseWifiScreen.name -> ChooseWifiScreen
            PreviewGridScreen.name -> PreviewGridScreen
            null -> HomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}
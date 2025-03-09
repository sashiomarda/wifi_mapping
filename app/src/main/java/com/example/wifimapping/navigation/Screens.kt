package com.example.wifimapping.navigation

enum class  Screens{
    HomeScreen,
    ChooseWifiScreen,
    PreviewGridScreen,
    LocateRouterScreen;
    companion object {
        fun fromRoute(route: String?): Screens
                = when (route?.substringBefore("/")){
            HomeScreen.name -> HomeScreen
            ChooseWifiScreen.name -> ChooseWifiScreen
            PreviewGridScreen.name -> PreviewGridScreen
            LocateRouterScreen.name -> LocateRouterScreen
            null -> HomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}
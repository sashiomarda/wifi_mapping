package com.example.wifimapping.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wifimapping.screens.chooseWifi.ChooseWifiScreen
import com.example.wifimapping.screens.home.HomeScreen
import com.example.wifimapping.screens.previewGrid.PreviewGridScreen

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination = Screens.HomeScreen.name) {
        composable(Screens.HomeScreen.name){
            HomeScreen(navController = navController)
        }
        composable(Screens.ChooseWifiScreen.name+"/{length}/{width}/{grid}",
//            arguments = listOf(navArgument(name = "length"){type = NavType.StringType})
        ){
                backStackEntry ->
            val length = backStackEntry.arguments?.getString("length")
            val width = backStackEntry.arguments?.getString("width")
            val grid = backStackEntry.arguments?.getString("grid")
            var inputData = listOf(
                length,
                width,
                grid
            )
            ChooseWifiScreen(navController,inputData)
        }
        composable(Screens.PreviewGridScreen.name+"/{length}/{width}/{grid}",
//            arguments = listOf(navArgument(name = "length"){type = NavType.StringType})
        ){
                backStackEntry ->
            val length = backStackEntry.arguments?.getString("length")
            val width = backStackEntry.arguments?.getString("width")
            val grid = backStackEntry.arguments?.getString("grid")
            var inputData = listOf(
                length,
                width,
                grid
            )
            PreviewGridScreen(navController,inputData)
        }
    }
}
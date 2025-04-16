/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.wifimapping.ui.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.wifimapping.ui.chooseWifi.ChooseWifiDestination
import com.example.wifimapping.ui.chooseWifi.ChooseWifiScreen
import com.example.wifimapping.ui.collectData.CollectDataDestination
import com.example.wifimapping.ui.collectData.CollectDataScreen
import com.example.wifimapping.ui.history.HistoryDestination
import com.example.wifimapping.ui.history.HistoryScreen
import com.example.wifimapping.ui.home.HomeDestination
import com.example.wifimapping.ui.home.HomeScreen
import com.example.wifimapping.ui.itemEntry.ItemEntryDestination
import com.example.wifimapping.ui.itemEntry.ItemEntryScreen
import com.example.wifimapping.ui.locateRouter.LocateRouterDestination
import com.example.wifimapping.ui.locateRouter.LocateRouterScreen
import com.example.wifimapping.ui.previewGrid.PreviewGridDestination
import com.example.wifimapping.ui.previewGrid.PreviewGridScreen
import com.example.wifimapping.ui.roomList.RoomListDestination
import com.example.wifimapping.ui.roomList.RoomListScreen

/**
 * Provides Navigation graph for the application.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun WifiMappingNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToNextMenu = {
                    if (it == RoomListDestination.route) {
                        navController.navigate("$it/0")
                    }else{
                        navController.navigate(it)
                    }
                                     },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(route = RoomListDestination.routeWithArgs,
            arguments = listOf(navArgument(RoomListDestination.idCollectData) {
                type = NavType.IntType
            })) {
            RoomListScreen(
//                navigateToNextMenu = { navController.navigate(it) },
                navigateToRoomParamsEntry = { navController.navigate(ItemEntryDestination.route) },
                onNavigateUp = { navController.navigateUp() },
                navigateToPreviewGrid = {navController.navigate("${PreviewGridDestination.route}/${it}") },
            )
        }
        composable(route = HistoryDestination.route) {
            HistoryScreen(
//                navigateToNextMenu = { navController.navigate(it) },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(route = ItemEntryDestination.route) {
            ItemEntryScreen(
                navigateToPreviewGrid = { navController.navigate("${PreviewGridDestination.route}/0") },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = PreviewGridDestination.routeWithArgs,
            arguments = listOf(navArgument(PreviewGridDestination.idCollectData) {
                type = NavType.IntType
            })
        ) {
            PreviewGridScreen(
                navigateToChooseWifi = { navController.navigate("${ChooseWifiDestination.route}/0") },
            )
        }

        composable(route = ChooseWifiDestination.routeWithArgs,
            arguments = listOf(navArgument(ChooseWifiDestination.idCollectData) {
                type = NavType.IntType
            })
        ) {
            ChooseWifiScreen(
                navigateToLocateRouter = {
                    navController.navigate("${LocateRouterDestination.route}/${it}")
                                         },
            )
        }


        composable(
            route = LocateRouterDestination.routeWithArgs,
            arguments = listOf(navArgument(LocateRouterDestination.idCollectData) {
                type = NavType.IntType
            })
        ) {
            LocateRouterScreen(
                navigateToCollectData= { navController.navigate("${CollectDataDestination.route}/${it}") },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = CollectDataDestination.routeWithArgs,
            arguments = listOf(navArgument(CollectDataDestination.idCollectData) {
                type = NavType.IntType
            })
        ) {
            CollectDataScreen(
//                navigateToCollectData= { navController.navigate(PreviewGridDestination.route) },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}

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

package com.sashiomarda.wifimapping.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sashiomarda.wifimapping.ui.chooseWifi.ChooseWifiDestination
import com.sashiomarda.wifimapping.ui.chooseWifi.ChooseWifiScreen
import com.sashiomarda.wifimapping.ui.collectData.CollectDataDestination
import com.sashiomarda.wifimapping.ui.collectData.CollectDataScreen
import com.sashiomarda.wifimapping.ui.history.HistoryDestination
import com.sashiomarda.wifimapping.ui.history.HistoryScreen
import com.sashiomarda.wifimapping.ui.home.HomeDestination
import com.sashiomarda.wifimapping.ui.home.HomeScreen
import com.sashiomarda.wifimapping.ui.itemEntry.ItemEntryDestination
import com.sashiomarda.wifimapping.ui.itemEntry.ItemEntryScreen
import com.sashiomarda.wifimapping.ui.locateRouter.LocateRouterDestination
import com.sashiomarda.wifimapping.ui.locateRouter.LocateRouterScreen
import com.sashiomarda.wifimapping.ui.previewGrid.PreviewGridDestination
import com.sashiomarda.wifimapping.ui.previewGrid.PreviewGridScreen
import com.sashiomarda.wifimapping.ui.roomList.RoomListDestination
import com.sashiomarda.wifimapping.ui.roomList.RoomListScreen

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
                navigateToNextMenu = {navController.navigate("$it/0")},
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(route = RoomListDestination.routeWithArgs,
            arguments = listOf(navArgument(RoomListDestination.idHistory) {
                type = NavType.IntType
            })) {
            RoomListScreen(
                navigateToRoomParamsEntry = { navController.navigate("${ItemEntryDestination.route}/0") },
                onNavigateUp = { navController.navigateUp() },
                navigateToHistory = { navController.navigate("${HistoryDestination.route}/${it}") },
            )
        }
        composable(route = HistoryDestination.routeWithArgs,
            arguments = listOf(navArgument(HistoryDestination.idRoom) {
                type = NavType.IntType
            })) {
            HistoryScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateToRoomPreviewGrid = { navController.navigate("${PreviewGridDestination.route}/${it}") },
            )
        }
        composable(route = ItemEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(ItemEntryDestination.idRoom) {
                type = NavType.IntType
            })) {
            ItemEntryScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateBack = { navController.popBackStack() },
            )
        }
        composable(
            route = PreviewGridDestination.routeWithArgs,
            arguments = listOf(navArgument(PreviewGridDestination.idHistory) {
                type = NavType.IntType
            })
        ) {
            PreviewGridScreen(
                navigateToChooseWifi = { navController.navigate("${ChooseWifiDestination.route}/${it}") },
            )
        }

        composable(route = ChooseWifiDestination.routeWithArgs,
            arguments = listOf(navArgument(ChooseWifiDestination.idHistory) {
                type = NavType.IntType
            })
        ) {
            ChooseWifiScreen(
                navigateToLocateRouter = {navController.navigate("${LocateRouterDestination.route}/${it}")},
            )
        }


        composable(
            route = LocateRouterDestination.routeWithArgs,
            arguments = listOf(navArgument(LocateRouterDestination.idHistory) {
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
            arguments = listOf(navArgument(CollectDataDestination.idHistory) {
                type = NavType.IntType
            })
        ) {
            CollectDataScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}

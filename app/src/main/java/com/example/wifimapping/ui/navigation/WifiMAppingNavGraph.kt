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
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wifimapping.screens.locateRouter.LocateRouterDestination
import com.example.wifimapping.screens.locateRouter.LocateRouterScreen
import com.example.wifimapping.ui.chooseWifi.ChooseWifiDestination
import com.example.wifimapping.ui.chooseWifi.ChooseWifiScreen
import com.example.wifimapping.ui.collectData.CollectDataDestination
import com.example.wifimapping.ui.collectData.CollectDataScreen
import com.example.wifimapping.ui.home.ItemEntryDestination
import com.example.wifimapping.ui.home.ItemEntryScreen
import com.example.wifimapping.ui.previewGrid.PreviewGridDestination
import com.example.wifimapping.ui.previewGrid.PreviewGridScreen

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
        startDestination = ItemEntryDestination.route,
        modifier = modifier
    ) {
        composable(route = ItemEntryDestination.route) {
            ItemEntryScreen(
                navigateToPreviewGrid = { navController.navigate(PreviewGridDestination.route) },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(route = PreviewGridDestination.route) {
            PreviewGridScreen(
                navigateToChooseWifi = { navController.navigate(ChooseWifiDestination.route) },
            )
        }

        composable(route = ChooseWifiDestination.route) {
            ChooseWifiScreen(
                navigateToLocateRouter = { navController.navigate(LocateRouterDestination.route) },
            )
        }


        composable(route = LocateRouterDestination.route) {
            LocateRouterScreen(
                navigateToCollectData= { navController.navigate(CollectDataDestination.route) },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(route = CollectDataDestination.route) {
            CollectDataScreen(
//                navigateToCollectData= { navController.navigate(PreviewGridDestination.route) },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}

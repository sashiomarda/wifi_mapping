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

package com.sashiomarda.wifimapping.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sashiomarda.wifimapping.WifiMappingTopAppBar
import com.sashiomarda.wifimapping.R
import com.sashiomarda.wifimapping.ui.history.HistoryDestination
import com.sashiomarda.wifimapping.ui.navigation.NavigationDestination
import com.sashiomarda.wifimapping.ui.roomList.RoomListDestination

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.room_params_entry_title
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToNextMenu: (String) -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = false,
    displayName: String?,
    navigateToProfile: () -> Unit,
) {
    var isShowOnboarding by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            WifiMappingTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxWidth()
            .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                ) {
                    Button(
                        onClick = navigateToProfile
                    ) {
                        Icon(Icons.Filled.Person, contentDescription = "profile")
                    }
                    Text(
                        "Halo, ${displayName}!",
                        modifier = Modifier
                            .padding(bottom = 15.dp)
                    )
                }
                Text(
                    "Beranda",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                )
                val menuNameList = listOf<String>(
                    "Ruangan",
                    "Riwayat"
                )
                val nextRouteList = listOf<String>(
                    RoomListDestination.route,
                    HistoryDestination.route
                )
                val imageVectorList = listOf<ImageVector>(
                    Icons.Default.Home,
                    Icons.Default.Menu
                )
                val menuList: MutableList<HomeMenu> = ArrayList()
                for (i in menuNameList.indices) {
                    menuList.add(
                        HomeMenu(
                            menuName = menuNameList[i],
                            nextRoute = nextRouteList[i],
                            imageVector = imageVectorList[i]
                        )
                    )
                }
                LazyColumn {
                    items(items = menuList) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .border(border = BorderStroke(2.dp, color = Color.LightGray))
                                .clickable {
                                    navigateToNextMenu(it.nextRoute)
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(it.imageVector, contentDescription = it.menuName)
                            Text(it.menuName)
                        }
                        Spacer(
                            modifier = Modifier
                                .height(10.dp)
                        )
                    }
                }
            }
        }
        if (isShowOnboarding) {
            OnboardingPermissionScreen { ->
                isShowOnboarding = false
            }
        }
    }
}

@Composable
fun OnboardingPermissionScreen(onContinue: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Optional icon/logo
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = "App Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Selamat Datang di Aplikasi WiFi Mapping!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = """
                    Agar aplikasi berfungsi dengan optimal, Anda perlu:
                    
                    • Mengizinkan akses lokasi
                    • Menyalakan Mode Pengembang
                    • Menonaktifkan pembatasan pemindaian WiFi
                    • Mengizinkan penyimpanan file untuk menyimpan gambar peta
                    
                    Pastikan semua pengaturan di atas diaktifkan agar pemindaian jaringan berjalan lancar dan akurat.
                """.trimIndent(),
                fontSize = 16.sp,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lanjutkan")
            }
        }
    }
}

data class HomeMenu(
    val menuName: String,
    val nextRoute: String,
    val imageVector: ImageVector
)
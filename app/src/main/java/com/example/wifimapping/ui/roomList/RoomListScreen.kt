package com.example.wifimapping.ui.roomList

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wifimapping.WifiMappingTopAppBar
import com.example.wifimapping.R
import com.example.wifimapping.data.RoomParams
import com.example.wifimapping.ui.AppViewModelProvider
import com.example.wifimapping.ui.itemEntry.ItemEntryDestination
import com.example.wifimapping.ui.navigation.NavigationDestination
import com.example.wifimapping.ui.viewmodel.RoomParamsViewModel

object RoomListDestination : NavigationDestination {
    override val route = "room_list"
    override val titleRes = R.string.locate_router_title
    const val idHistory = "idHistory"
    val routeWithArgs = "${route}/{$idHistory}"
}

@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomListScreen(
    navigateToRoomParamsEntry: () -> Unit,
    navigateToHistory: (Int) -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = false,
    roomParamsViewModel: RoomParamsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val wifiUiStateList by roomParamsViewModel.allRoomUiStateList.collectAsState()
    Scaffold(
        topBar = {
            WifiMappingTopAppBar(
                title = stringResource(ItemEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navigateToRoomParamsEntry()
                          },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Room"
                )
            }
        },
    ) { innerPadding ->
        Surface(modifier = Modifier
            .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Ruangan",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                )
                Card(
                    modifier = Modifier
                        .padding(10.dp)
                        .height(500.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(corner = CornerSize(16.dp))
                ) {
                    if (wifiUiStateList.roomParamList.isEmpty()) {
                        Text(
                            modifier = Modifier
                                .padding(10.dp),
                            text = "Belum ada ruangan ditambahkan"
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .padding(10.dp)
                        ) {
                            items(
                                items = wifiUiStateList.roomParamList,
                                key = { roomParams: RoomParams ->
                                    roomParams.id
                                }) {
                                Card(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxWidth()
                                        .clickable {
//                                        navigateToPreviewGrid(it.id)
                                            navigateToHistory(it.id)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Column {
                                            Text(it.roomName)
//                                        var timestamp = TimeConverter().fromTimestamp(it.timestamp)
//                                        Text("$timestamp")
                                        }
                                    }
                                }
                                HorizontalDivider(
                                    color = Color.LightGray,
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
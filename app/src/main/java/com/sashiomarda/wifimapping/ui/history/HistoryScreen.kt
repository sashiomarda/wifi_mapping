package com.sashiomarda.wifimapping.ui.history

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sashiomarda.wifimapping.WifiMappingTopAppBar
import com.sashiomarda.wifimapping.R
import com.sashiomarda.wifimapping.data.HistoryRoom
import com.sashiomarda.wifimapping.ui.AppViewModelProvider
import com.sashiomarda.wifimapping.ui.roomInput.RoomInputDestination
import com.sashiomarda.wifimapping.ui.navigation.NavigationDestination
import com.sashiomarda.wifimapping.ui.viewmodel.HistoryRoomUiStateList
import com.sashiomarda.wifimapping.ui.viewmodel.HistoryViewModel
import com.sashiomarda.wifimapping.util.TimeConverter
import kotlinx.coroutines.launch

object HistoryDestination : NavigationDestination {
    override val route = "history"
    override val titleRes = R.string.locate_router_title
    const val idRoom = "idRoom"
    val routeWithArgs = "${route}/{$idRoom}"
}

@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateUp: () -> Unit,
    navigateToRoomPreviewGrid: (Int) -> Unit,
    canNavigateBack: Boolean = false,
    historyViewModel: HistoryViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    var historyList= HistoryRoomUiStateList().historyList
    val historyAllList by historyViewModel.historyAllUiStateList.collectAsState()
    val historyByIdList by historyViewModel.historyByIdUiStateList.collectAsState()
    historyList = historyAllList.historyList
    var isnavigateToRoomPreviewGrid: Boolean by remember { mutableStateOf(false) }
    if (historyViewModel.getIdRoom() != 0) {
        historyList = historyByIdList.historyList
    }
    if (isnavigateToRoomPreviewGrid && historyList.isNotEmpty()) {
        navigateToRoomPreviewGrid(historyList.last().id)
    }
    Scaffold(
        topBar = {
            WifiMappingTopAppBar(
                title = stringResource(RoomInputDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        if (historyViewModel.getIdRoom() != 0) {
                            historyViewModel.saveHistory(
                                historyViewModel.historyUiState.historyDetails.copy(
                                    idRoom = historyViewModel.getIdRoom(),
                                    timestamp = System.currentTimeMillis(),
                                )
                            )
                            isnavigateToRoomPreviewGrid = true
                        }
                    }
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
                    "Riwayat",
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
                    if (historyList.isEmpty()) {
                        Text(
                            modifier = Modifier
                                .padding(10.dp),
                            text = "Belum ada Riwayat Pengukuran"
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .padding(20.dp)
                        ) {
                            items(
                                items = historyList,
                                key = { historyRoom: HistoryRoom ->
                                    historyRoom.id
                                }
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navigateToRoomPreviewGrid(it.id)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Column {
                                            val timeConverter = TimeConverter()
                                            Text(it.roomName, fontWeight = FontWeight.Bold)
                                            Text("${timeConverter.fromTimestamp(it.timestamp)}")
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
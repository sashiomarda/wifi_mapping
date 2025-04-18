package com.example.wifimapping.ui.locateRouter

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wifimapping.InventoryTopAppBar
import com.example.wifimapping.R
import com.example.wifimapping.components.CanvasGrid
import com.example.wifimapping.data.Wifi
import com.example.wifimapping.ui.AppViewModelProvider
import com.example.wifimapping.ui.home.ItemEntryDestination
import com.example.wifimapping.ui.navigation.NavigationDestination
import com.example.wifimapping.ui.previewGrid.vertical
import com.example.wifimapping.ui.viewmodel.DbmViewModel
import com.example.wifimapping.ui.viewmodel.GridViewModel
import com.example.wifimapping.ui.viewmodel.RoomParamsViewModel
import com.example.wifimapping.ui.viewmodel.WifiViewModel
import kotlinx.coroutines.launch
import kotlin.Boolean
import kotlin.Unit

object LocateRouterDestination : NavigationDestination {
    override val route = "locate_router"
    override val titleRes = R.string.locate_router_title
    const val idCollectData = "idCollectData"
    val routeWithArgs = "${route}/{$idCollectData}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocateRouterScreen(
    navigateToCollectData: (Int) -> Unit,
    previewGridViewModel: RoomParamsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    wifiViewModel: WifiViewModel = viewModel(factory = AppViewModelProvider.Factory),
    gridViewModel: GridViewModel = viewModel(factory = AppViewModelProvider.Factory),
    dbmViewModel: DbmViewModel = viewModel(factory = AppViewModelProvider.Factory),
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
){
    val coroutineScope = rememberCoroutineScope()
    val wifiCheckedUiStateList by wifiViewModel.wifiCheckedUiStateList.collectAsState()
    var data = previewGridViewModel.roomParamsUiState.roomParamsDetails
    var chosenIdSsid by remember { mutableStateOf(0) }
    var chosenIdSsidList = remember { mutableStateListOf<Int>() }
    var chosenSsidList = remember { mutableStateListOf<String>() }
    var chosenIdGridList = remember { mutableStateListOf<Int>() }
    var isResetChosenIdSsid by remember { mutableStateOf(false) }
    var idGridRouterPosition by remember { mutableStateOf(0) }
    val gridListDb by gridViewModel.gridUiStateList.collectAsState()
    val firstGridId = if (gridListDb.gridList.isNotEmpty()) gridListDb.gridList[0].id else 1
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(ItemEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
            ){
                Text("Locate Router Position",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(bottom = 15.dp))
                Box(modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp)) {
                    WifiCheckedList(
                        wifiCheckListDb = wifiCheckedUiStateList.wifiList,
                        saveCurrentChosenIdSsid = {
                            chosenIdSsid = it
                            isResetChosenIdSsid = false
                        },
                        isResetChosenIdSsid = isResetChosenIdSsid,
                        chosenIdSsidList = chosenIdSsidList
                    )
                }
                if (data.length != "") {
                    Text("Panjang ${data.length} m")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(
                            modifier = Modifier
                        ) {
                            Text(
                                modifier = Modifier
                                    .vertical()
                                    .rotate(-90f),
                                text = "Lebar ${data.width} m"
                            )
                        }
                        CanvasGrid(
                            length = data.length.toFloat(),
                            width = data.width.toFloat(),
                            grid = data.gridDistance.toInt(),
                            gridViewModel = gridViewModel,
                            chosenIdSsid = chosenIdSsid,
                            gridListDb = gridListDb,
                            saveIdGridRouterPosition = { it ->
                                idGridRouterPosition = it
                            },
                            screen = LocateRouterDestination.route,
                            dbmViewModel = dbmViewModel,
                            saveCanvasBitmap = {},
                            addChosenIdList = {ssidId, gridId ->
                                chosenIdSsidList.add(ssidId)
                                for (wifi in wifiCheckedUiStateList.wifiList) {
                                    if (wifi.id == ssidId) {
                                        chosenSsidList.add(wifi.ssid)
                                        chosenIdGridList.add(gridId)
                                    }
                                }
                            }
                        )
                    }
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp),
                        horizontalAlignment = Alignment.Start) {
                        if (idGridRouterPosition != 0) {
                            for (i in 0..chosenSsidList.size - 1) {
                                Text(
                                    modifier = Modifier
                                        .padding(top = 10.dp),
                                    text = "Grid ${chosenIdGridList[i] - firstGridId + 1} : ${chosenSsidList[i]}"
                                )
                            }
                        } else {
                            Text(
                                modifier = Modifier
                                    .padding(top = 10.dp),
                                text = ""
                            )
                        }
                    }
                    Row {
                        Button(modifier = Modifier
                            .padding(end = 3.dp),
                            shape = RoundedCornerShape(5.dp),
                            onClick = {
                                coroutineScope.launch {
                                    for (it in gridListDb.gridList) {
                                        gridViewModel.updateUiState(
                                            gridViewModel.gridUiState.gridDetails.copy(
                                                id = it.id,
                                                idCollectData = it.idCollectData,
                                                idWifi = 0,
                                                isClicked = false
                                            )
                                        )
                                        gridViewModel.updateGrid()
                                        chosenIdSsid = 0
                                        idGridRouterPosition = 0
                                        isResetChosenIdSsid = true
                                    }
                                    chosenIdSsidList.removeAll(chosenIdSsidList)
                                    chosenSsidList.removeAll(chosenSsidList)
                                }
                            }) {
                            Text("Reset Lokasi Router")
                        }

                        Button(
                            modifier = Modifier
                                .padding(start = 3.dp),
                            enabled = idGridRouterPosition != 0 && chosenIdSsid != 0,
                            shape = RoundedCornerShape(5.dp),
                            onClick = {
                                navigateToCollectData(gridListDb.gridList[0].idCollectData)
                            }) {
                            Text("Selanjutnya")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WifiCheckedList(
    wifiCheckListDb: List<Wifi>,
    saveCurrentChosenIdSsid: (Int) -> Unit,
    isResetChosenIdSsid: Boolean,
    chosenIdSsidList: SnapshotStateList<Int>,
){
    var wifiLocateList : MutableList<WifiLocateRouter> = ArrayList()
    for (wifi in wifiCheckListDb){
        wifiLocateList.add(wifi.toWifiLocateRouter().copy(isChosenLocationRouter = false))
    }
    var isChosenIdSSid by remember { mutableStateOf(0) }
    LazyColumn(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .heightIn(min = 80.dp, max = 500.dp)
    ) {
        items(items = wifiLocateList) {
            Surface(
                color = if (isChosenIdSSid == it.id && isResetChosenIdSsid == false) {
                    Color(0xFF464646)
                } else Color.Transparent,
                ) {
                Column {
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clickable(
                                enabled = if (it.id in chosenIdSsidList){
                                    false
                                }else{
                                    true
                                }
                            ) {
                                isChosenIdSSid = it.id
                                saveCurrentChosenIdSsid(it.id)
                            },
                        colors = CardDefaults.cardColors(Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                        ) {
                            Text(
                                text = "${it.ssid}",
                                fontWeight = if (isChosenIdSSid == it.id) FontWeight.Bold else FontWeight.Light,
                                color = if (it.id in chosenIdSsidList){
                                    Color(0xFF333333)
                                }else{
                                    Color.Unspecified
                                }
                            )
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

fun Modifier.vertical() =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            )
        }
    }

data class WifiLocateRouter(
    var id: Int = 0,
    var ssid: String,
    var isCheckedDb: Boolean = false,
    var dbm: Int,
    var isChosenLocationRouter: Boolean = false,
)

fun Wifi.toWifiLocateRouter(): WifiLocateRouter = WifiLocateRouter(
    id = id,
    ssid = ssid,
    isCheckedDb = isChecked,
    dbm = dbm
)
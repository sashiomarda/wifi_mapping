package com.sashiomarda.wifimapping.ui.locateRouter

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sashiomarda.wifimapping.WifiMappingTopAppBar
import com.sashiomarda.wifimapping.R
import com.sashiomarda.wifimapping.components.CanvasGrid
import com.sashiomarda.wifimapping.components.DropDownMenu
import com.sashiomarda.wifimapping.data.Grid
import com.sashiomarda.wifimapping.data.Wifi
import com.sashiomarda.wifimapping.ui.AppViewModelProvider
import com.sashiomarda.wifimapping.ui.roomInput.RoomInputDestination
import com.sashiomarda.wifimapping.ui.navigation.NavigationDestination
import com.sashiomarda.wifimapping.ui.previewGrid.vertical
import com.sashiomarda.wifimapping.ui.viewmodel.DbmViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.GridViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.RoomParamsViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.WifiViewModel
import kotlinx.coroutines.launch
import kotlin.Boolean
import kotlin.Unit

object LocateRouterDestination : NavigationDestination {
    override val route = "locate_router"
    override val titleRes = R.string.locate_router_title
    const val idHistory = "idHistory"
    val routeWithArgs = "${route}/{$idHistory}"
}

@RequiresApi(Build.VERSION_CODES.O)
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
    var data = previewGridViewModel.roomParamByIdsUiState.roomParamsDetails
    var chosenIdSsid by remember { mutableStateOf(0) }
    var chosenIdSsidList = remember { mutableStateListOf<Int>() }
    var chosenSsidList = remember { mutableStateListOf<String>() }
    var isResetChosenIdSsid by remember { mutableStateOf(false) }
    var isFirstLoadGridList by remember { mutableStateOf(true) }
    var idGridRouterPosition by remember { mutableStateOf(0) }
    val gridListDb by gridViewModel.gridUiStateList.collectAsState()
    var gridList by remember { mutableStateOf(listOf(Grid())) }
    var firstGridId by remember { mutableStateOf(1) }
    var selectedLayer by remember { mutableStateOf(1) }
    if (gridListDb.gridList.isNotEmpty()){
        if (isFirstLoadGridList) {
            gridList = gridListDb.gridList
            if (gridList[0].layerNo == 1) {
                firstGridId = gridList[0].id
            }
        }
    }
    val allWifiUiStateList by wifiViewModel.allWifiUiStateList.collectAsState()
    var routerPositions by remember { mutableStateOf(emptyList<RouterPosition>()) }
    if (data.layerCount!="") {
        for (i in 1..data.layerCount.toInt()) {
            LaunchedEffect(gridViewModel) {
                val gridLayer = gridViewModel.getGridByLayerNo(i)
                for (grid in gridLayer){
                    if (grid.idWifi != 0){
                        val foundRouter = allWifiUiStateList.wifiList.firstOrNull{it.id == grid.idWifi}
                        if (foundRouter != null){
                            routerPositions = routerPositions + RouterPosition(
                                layer = grid.layerNo,
                                grid = grid.id,
                                ssidId = foundRouter.id,
                                ssid = foundRouter.ssid
                            )
                        }
                    }
                }
            }
        }
    }
    Scaffold(
        topBar = {
            WifiMappingTopAppBar(
                title = stringResource(RoomInputDestination.titleRes),
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
                Text("Posisi Router",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(bottom = 15.dp))
                Box(modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp, bottom = 15.dp)
                ) {
                    WifiCheckedList(
                        wifiCheckListDb = wifiCheckedUiStateList.wifiList,
                        saveCurrentChosenIdSsid = {
                            chosenIdSsid = it
                            isResetChosenIdSsid = false
                        },
                        isResetChosenIdSsid = isResetChosenIdSsid,
                        chosenIdSsidList = chosenIdSsidList,
                        routerPositions = routerPositions
                    )
                }
                if (data.length != "") {
                    Card(modifier = Modifier) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 40.dp,
                                        top = 5.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                ) {
                                    if (data.layerCount != "") {
                                        val menuItemData = List(data.layerCount.toInt()) { it + 1 }
                                        DropDownMenu(
                                            menuItemData = menuItemData,
                                            selectedLayer = {
                                                coroutineScope.launch {
                                                    isFirstLoadGridList = false
                                                    gridList = gridViewModel.getGridByLayerNo(it)
                                                    selectedLayer = it
                                                }
                                            }
                                        )
                                    }
                                }
                            }
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
                                    gridListDb = gridList,
                                    selectedLayer = selectedLayer,
                                    saveIdGridRouterPosition = { it ->
                                        idGridRouterPosition = it
                                        coroutineScope.launch {
                                            gridList = gridViewModel.getGridByLayerNo(1)
                                        }
                                    },
                                    screen = LocateRouterDestination.route,
                                    dbmViewModel = dbmViewModel,
                                    saveCanvasBitmap = {},
                                    addChosenIdList = { ssidId, gridId ->
                                        chosenIdSsidList.add(ssidId)
                                        for (wifi in wifiCheckedUiStateList.wifiList) {
                                            if (wifi.id == ssidId) {
                                                val foundSsid = routerPositions.firstOrNull{ it.ssid == wifi.ssid}
                                                if (foundSsid == null) {
                                                    routerPositions =
                                                        routerPositions + RouterPosition(
                                                            layer = gridList[0].layerNo,
                                                            grid = gridId,
                                                            ssidId = wifi.id,
                                                            ssid = wifi.ssid
                                                        )
                                                }
                                            }
                                        }
                                    },
                                    updateGridList = {
                                        coroutineScope.launch {
                                            gridList = gridViewModel.getGridByLayerNo(it)
                                        }
                                    },
                                    routerPositions = routerPositions
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 40.dp, top = 10.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                if (routerPositions.isNotEmpty()) {
                                    for (i in routerPositions.indices) {
                                        Text(
                                            modifier = Modifier,
                                            text = "Layer ${routerPositions[i].layer} " +
                                                    "Grid ${(routerPositions[i].grid - (firstGridId + (4 * (routerPositions[i].layer - 1))) + 1)} : " +
                                                    routerPositions[i].ssid,
                                            fontSize = 12.sp
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
                            Row(modifier = Modifier
                                .padding(bottom = 5.dp)) {
                                Button(
                                    modifier = Modifier
                                        .padding(end = 3.dp),
                                    shape = RoundedCornerShape(5.dp),
                                    onClick = {
                                        coroutineScope.launch {
                                            for (wifi in wifiCheckedUiStateList.wifiList){
                                                for (layerNo in 1..data.layerCount.toInt()) {
                                                    gridList = gridViewModel.getGridByLayerNo(layerNo)
                                                    val foundWifiGrid =
                                                        gridList.firstOrNull { it.idWifi != 0 }
                                                    if (foundWifiGrid != null) {
                                                        gridViewModel.updateUiState(
                                                            gridViewModel.gridUiState.gridDetails.copy(
                                                                id = foundWifiGrid.id,
                                                                idRoom = foundWifiGrid.idRoom,
                                                                idHistory = foundWifiGrid.idHistory,
                                                                idWifi = 0,
                                                                isClicked = false,
                                                                layerNo = foundWifiGrid.layerNo
                                                            )
                                                        )
                                                        gridViewModel.updateGrid()
                                                        chosenIdSsid = 0
                                                        idGridRouterPosition = 0
                                                        isResetChosenIdSsid = true
                                                    }
                                                }
                                            }
                                            chosenIdSsidList.removeAll(chosenIdSsidList)
                                            chosenSsidList.removeAll(chosenSsidList)
                                            routerPositions = emptyList()
                                            gridList = gridViewModel.getGridByLayerNo(selectedLayer)
                                        }
                                    }) {
                                    Text("Reset Lokasi Router")
                                }

                                Button(
                                    modifier = Modifier
                                        .padding(start = 3.dp),
                                    enabled = routerPositions.isNotEmpty(),
                                    shape = RoundedCornerShape(5.dp),
                                    onClick = {
                                        navigateToCollectData(gridListDb.gridList[0].idHistory)
                                    }) {
                                    Text("Selanjutnya")
                                }
                            }
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
    routerPositions: List<RouterPosition>,
){
    var wifiLocateList : MutableList<WifiLocateRouter> = ArrayList()
    for (wifi in wifiCheckListDb){
        wifiLocateList.add(wifi.toWifiLocateRouter().copy(isChosenLocationRouter = false))
    }
    var isChosenIdSSid by remember { mutableStateOf(0) }
    LazyColumn(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp)
            .fillMaxWidth()
            .heightIn(min = 80.dp, max = 500.dp)
    ) {
        items(items = wifiLocateList) { wifi ->
            Surface(
                color = if (isChosenIdSSid == wifi.id && isResetChosenIdSsid == false) {
                    Color(0xFF464646)
                } else Color.Transparent,
                ) {
                Column {
                    val foundWifiId = routerPositions.firstOrNull{it.ssidId == wifi.id}
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clickable(
                                enabled = foundWifiId == null
                            ) {
                                isChosenIdSSid = wifi.id
                                saveCurrentChosenIdSsid(wifi.id)
                            },
                        colors = CardDefaults.cardColors(Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                        ) {
                            Text(
                                text = wifi.ssid,
                                fontWeight = if (isChosenIdSSid == wifi.id) FontWeight.Bold else FontWeight.Light,
                                color = if (foundWifiId != null){
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
                    )
                }
            }
        }
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

data class RouterPosition(
    val layer: Int = 0,
    val grid: Int = 0,
    val ssidId: Int = 0,
    val ssid: String = ""
)
package com.example.wifimapping.ui.collectData

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wifimapping.InventoryTopAppBar
import com.example.wifimapping.R
import com.example.wifimapping.components.CanvasGrid
import com.example.wifimapping.data.Grid
import com.example.wifimapping.ui.AppViewModelProvider
import com.example.wifimapping.ui.home.ItemEntryDestination
import com.example.wifimapping.ui.navigation.NavigationDestination
import com.example.wifimapping.ui.viewmodel.DbmViewModel
import com.example.wifimapping.ui.viewmodel.GridUiStateList
import com.example.wifimapping.ui.viewmodel.GridViewModel
import com.example.wifimapping.ui.viewmodel.RoomParamsDetails
import com.example.wifimapping.ui.viewmodel.RoomParamsViewModel
import com.example.wifimapping.ui.viewmodel.WifiViewModel
import com.example.wifimapping.ui.viewmodel.toGrid
import com.example.wifimapping.ui.viewmodel.toWifi
import com.example.wifimapping.util.scanWifi
import kotlinx.coroutines.launch

object CollectDataDestination : NavigationDestination {
    override val route = "collect_data"
    override val titleRes = R.string.collect_data_title
    const val idCollectData = "idCollectData"
    val routeWithArgs = "${route}/{$idCollectData}"
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectDataScreen(
//    navigateToPreviewGrid: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = false,
    gridViewModel: GridViewModel = viewModel(factory = AppViewModelProvider.Factory),
    previewGridViewModel: RoomParamsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    dbmViewModel: DbmViewModel = viewModel(factory = AppViewModelProvider.Factory),
    wifiViewModel: WifiViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val context = LocalContext.current
    var wifiList = wifiViewModel.wifiScanList.wifiList
    val coroutineScope = rememberCoroutineScope()
    var data = previewGridViewModel.roomParamsUiState.roomParamsDetails
    var chosenIdSsid by remember { mutableStateOf(0) }
    val gridListDb by gridViewModel.gridUiStateList.collectAsState()
    val firstGridId = if (gridListDb.gridList.isNotEmpty()) gridListDb.gridList[0].id else 0
    val lastGridId = if (gridListDb.gridList.isNotEmpty()) gridListDb.gridList.last().id else 0
    var currentActiveGrid by remember { mutableStateOf(
        gridViewModel.currentGrid.toGrid()
    ) }
    var idGrids : MutableList<Int> = ArrayList()
    var ssidList : MutableList<String> = ArrayList()
    var dbmList : MutableList<Int> = ArrayList()
    if (gridListDb.gridList.isNotEmpty()) {
        if (currentActiveGrid.id == 0){
            currentActiveGrid = gridListDb.gridList[0]
        }
        for (i in gridListDb.gridList) {
            idGrids.add(i.id)
            if (i.idWifi != 0){
                chosenIdSsid = i.idWifi
            }
        }
    }
    var chosenSsid by remember { mutableStateOf(wifiViewModel.wifiUiState.wifiDetails.toWifi()) }
    var gridHaveDbm = remember { mutableStateListOf<Int>() }

    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(ItemEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Surface(modifier = Modifier
            .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Panjang ${data.length} m")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(
                            modifier = Modifier
                                .padding(start = 5.dp)
                        ) {
                            Text("Lebar")
                            Text("${data.width} m")
                        }
                        if (data.id != 0) {
                            CanvasGrid(
                                length = data.length.toFloat(),
                                width = data.width.toFloat(),
                                grid = data.gridDistance.toInt(),
                                gridViewModel = gridViewModel,
                                chosenIdSsid = chosenIdSsid,
                                gridListDb = gridListDb,
                                saveIdGridRouterPosition = {},
                                screen = CollectDataDestination.route,
                                dbmViewModel = dbmViewModel
                            )
                        }
                    }
                }
                Button(
                    modifier = Modifier
                        .size(50.dp),
//                            .padding(3.dp),
                    shape = RoundedCornerShape(50.dp),
                    onClick = {
                        var prevAndCurrentGrid = navButtonClick(
                            gridViewModel,
                            gridListDb,
                            data,
                            idGrids,
                            firstGridId,
                            lastGridId,
                            "up")

                        if (prevAndCurrentGrid.isMoveGrid) {
                            coroutineScope.launch {
                                gridViewModel.updateChosenGrid(
                                    prevAndCurrentGrid.previousActiveGrid.copy(isClicked = false),
                                    prevAndCurrentGrid.currentActiveGrid.copy(isClicked = true)
                                )
                            }
                        }
                    }
                ) {
                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "up arrow")
                }
                Row(modifier = Modifier
                    .padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        modifier = Modifier
                            .size(50.dp),
//                            .padding(3.dp),
                        shape = RoundedCornerShape(50.dp),
                        onClick = {
                            var prevAndCurrentGrid = navButtonClick(
                                gridViewModel,
                                gridListDb,
                                data,
                                idGrids,
                                firstGridId,
                                lastGridId,
                                "left")

                            if (prevAndCurrentGrid.isMoveGrid) {
                                coroutineScope.launch {
                                    gridViewModel.updateChosenGrid(
                                        prevAndCurrentGrid.previousActiveGrid.copy(isClicked = false),
                                        prevAndCurrentGrid.currentActiveGrid.copy(isClicked = true)
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "left arrow")
                    }
                    Button(
                        modifier = Modifier
                            .height(150.dp)
                            .width(200.dp),
//                            .padding(3.dp),
                        shape = RoundedCornerShape(50.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color.LightGray
                        ),
                        onClick = {
                            currentActiveGrid = gridViewModel.currentGrid.toGrid()
                            var scanWifiResult = scanWifi(context)
                            Log.d("scanwifi result", "${scanWifiResult}")
                            if (scanWifiResult.isNotEmpty()) {
                                wifiList = scanWifiResult
                            }else{
                                Toast.makeText(context,
                                    "Too fast clicking!",
                                    Toast.LENGTH_SHORT).show()
                            }
                            if (!wifiList.isNullOrEmpty()) {
                                for (i in wifiList) {
                                    ssidList.add(i.ssid)
                                    dbmList.add(i.dbm)
                                }
                            }
                            coroutineScope.launch {
                                if (currentActiveGrid.id == 0){
                                    currentActiveGrid = gridListDb.gridList[0]
                                }
                                if (chosenIdSsid != 0) {
                                    chosenSsid = wifiViewModel.selectWifiById(chosenIdSsid)
                                }
                                var dbm = dbmList[
                                    ssidList.indexOf(chosenSsid.ssid)
                                ]
                                var inputDbm = dbmViewModel.dbmUiState.dbmDetails.copy(
                                    idCollectData = data.id,
                                    idGrid = currentActiveGrid.id,
                                    dbm = dbm
                                )
                                if (currentActiveGrid.id !in gridHaveDbm) {
                                    dbmViewModel.saveDbm(inputDbm)
                                    Toast.makeText(context,
                                        "${dbm}",
                                        Toast.LENGTH_SHORT).show()
                                    gridHaveDbm.add(currentActiveGrid.id)
                                }
                            }
                        },
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Pastikan HP dalam posisi stabil dan tidak bergerak",
                                textAlign = TextAlign.Center,
                                        fontSize = 12.sp)
                            var changeBtnText = ""
                            changeBtnText = "Ambil data"
                            Text(modifier = Modifier
                                .padding(top = 10.dp), text = "${changeBtnText}",
                                fontSize = 20.sp)
                        }
                    }
                    Button(
                        modifier = Modifier
                            .size(50.dp),
//                            .padding(3.dp),
                        shape = RoundedCornerShape(50.dp),
                        onClick = {
                            var prevAndCurrentGrid = navButtonClick(
                                gridViewModel,
                                gridListDb,
                                data,
                                idGrids,
                                firstGridId,
                                lastGridId,
                                "right")

                            if (prevAndCurrentGrid.isMoveGrid) {
                                coroutineScope.launch {
                                    gridViewModel.updateChosenGrid(
                                        prevAndCurrentGrid.previousActiveGrid.copy(isClicked = false),
                                        prevAndCurrentGrid.currentActiveGrid.copy(isClicked = true)
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "right arrow")
                    }
                }
                Button(
                    modifier = Modifier
                        .size(50.dp),
//                            .padding(3.dp),
                    shape = RoundedCornerShape(50.dp),
                    onClick = {
                        var prevAndCurrentGrid = navButtonClick(
                            gridViewModel,
                            gridListDb,
                            data,
                            idGrids,
                            firstGridId,
                            lastGridId,
                            "down")

                        if (prevAndCurrentGrid.isMoveGrid) {
                            coroutineScope.launch {
                                gridViewModel.updateChosenGrid(
                                    prevAndCurrentGrid.previousActiveGrid.copy(isClicked = false),
                                    prevAndCurrentGrid.currentActiveGrid.copy(isClicked = true)
                                )
                            }
                        }
                    }
                ) {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "down arrow")
                }
                Button(
                    modifier = Modifier
                        .padding(5.dp),
                    shape = RoundedCornerShape(50.dp),
                    enabled = false,
                    onClick = {}
                ) {
                    Text("Simpan gambar peta")
                }
            }
        }
    }
}


private fun navButtonClick(
    gridViewModel: GridViewModel,
    gridListDb: GridUiStateList,
    data: RoomParamsDetails,
    idGrids: MutableList<Int>,
    firstGridId: Int,
    lastGridId: Int,
    direction: String
): PrevAndCurrentGrid {
    var currentActiveGrid = gridViewModel.currentGrid.toGrid()
    if (currentActiveGrid.id == 0){
        currentActiveGrid = gridListDb.gridList[0]
    }
    var previousActiveGrid = gridViewModel.previousGrid.toGrid()
    var isMoveGrid = false
    var currentActiveGridPosition = currentActiveGrid.id - firstGridId + 1
    var chosenIdGrid = 0
    if (direction == "up") {
        if (currentActiveGridPosition - data.length.toInt() >= 1) {
            isMoveGrid = true
            chosenIdGrid = currentActiveGrid.id - data.length.toInt()
        }
    } else if (direction == "left") {
        if (currentActiveGridPosition > 1) {
            isMoveGrid = true
            chosenIdGrid = currentActiveGrid.id - 1
        }
    } else if (direction == "right") {
        if (currentActiveGrid.id == 0){
            currentActiveGrid = gridListDb.gridList[0]
            currentActiveGridPosition = gridListDb.gridList[0].id - firstGridId + 1
        }
        if (currentActiveGridPosition < lastGridId - firstGridId + 1) {
            isMoveGrid = true
            chosenIdGrid = currentActiveGrid.id + 1
        }
    } else if (direction == "down") {
        if (currentActiveGridPosition + data.length.toInt()  <= lastGridId - firstGridId + 1) {
            isMoveGrid = true
            chosenIdGrid = currentActiveGrid.id + data.length.toInt()
        }
    }
    Log.d("chosenIdGrid","${currentActiveGrid.id} ${data.length.toInt()}")
    Log.d("chosenIdGrid","${chosenIdGrid}")
    Log.d("idGrids","${idGrids}")
    Log.d("gridListDb.gridList","${gridListDb.gridList}")

    if (isMoveGrid){
        previousActiveGrid = currentActiveGrid
        currentActiveGrid = gridListDb.gridList[
            idGrids.indexOf(chosenIdGrid)
        ].copy(isClicked = false)
    }

    return PrevAndCurrentGrid(previousActiveGrid, currentActiveGrid, isMoveGrid)
}

data class PrevAndCurrentGrid(
    val previousActiveGrid: Grid,
    val currentActiveGrid: Grid,
    val isMoveGrid: Boolean
)
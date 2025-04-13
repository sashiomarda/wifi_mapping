package com.example.wifimapping.ui.collectData

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.createChooser
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wifimapping.InventoryTopAppBar
import com.example.wifimapping.R
import com.example.wifimapping.components.CanvasGrid
import com.example.wifimapping.data.Grid
import com.example.wifimapping.ui.AppViewModelProvider
import com.example.wifimapping.ui.home.ItemEntryDestination
import com.example.wifimapping.ui.navigation.NavigationDestination
import com.example.wifimapping.ui.previewGrid.vertical
import com.example.wifimapping.ui.viewmodel.DbmViewModel
import com.example.wifimapping.ui.viewmodel.GridUiStateList
import com.example.wifimapping.ui.viewmodel.GridViewModel
import com.example.wifimapping.ui.viewmodel.RoomParamsDetails
import com.example.wifimapping.ui.viewmodel.RoomParamsViewModel
import com.example.wifimapping.ui.viewmodel.WifiViewModel
import com.example.wifimapping.ui.viewmodel.toGrid
import com.example.wifimapping.ui.viewmodel.toWifi
import com.example.wifimapping.util.ObserveChosenSsidDbm
import com.example.wifimapping.util.getDbm
import com.example.wifimapping.util.scanWifi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File

object CollectDataDestination : NavigationDestination {
    override val route = "collect_data"
    override val titleRes = R.string.collect_data_title
    const val idCollectData = "idCollectData"
    val routeWithArgs = "${route}/{$idCollectData}"
}

@SuppressLint("UnrememberedMutableState")
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
    var chosenIdSsid by remember { mutableIntStateOf(0) }
    var dbmText by remember { mutableStateOf("") }
    var dbmObserveInt by remember { mutableStateOf(0)}
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
    var observeChosenSsidDbm = ObserveChosenSsidDbm(context, chosenSsid, gridListDb, wifiViewModel)
    var imageBitmap by mutableStateOf(ImageBitmap(500,500))
    var isSaveImageButton by remember { mutableStateOf(false)}
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
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                                dbmViewModel = dbmViewModel,
                                saveCanvasBitmap = {bitmap ->
                                    imageBitmap = bitmap
                                    if (gridHaveDbm.size == gridListDb.gridList.size) {
                                        isSaveImageButton = true
                                    }
                                }
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 20.dp, end = 20.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF1AFF00))
                                    .size(15.dp)
                            )
                            Text(
                                modifier = Modifier
                                    .padding(start = 5.dp),
                                fontSize = 10.sp,
                                text = "> -67 dbm (sangat kuat)"
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFFEB3B))
                                    .size(15.dp)
                            )
                            Text(
                                modifier = Modifier
                                    .padding(start = 5.dp),
                                fontSize = 10.sp,
                                text = "-68 dbm s/d -70 dbm (kuat)"
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFF9800))
                                    .size(15.dp)
                            )
                            Text(
                                modifier = Modifier
                                    .padding(start = 5.dp),
                                fontSize = 10.sp,
                                text = "-71 dbm s/d -80 dbm (lemah)"
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFF0000))
                                    .size(15.dp)
                            )
                            Text(
                                modifier = Modifier
                                    .padding(start = 5.dp),
                                fontSize = 10.sp,
                                text = "< -80 dbm (sangat lemah)"
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        val activeGridText = if (currentActiveGrid.id != 0) {
                            currentActiveGrid.id - firstGridId + 1
                        } else {
                            1
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Posisi Grid Aktif: ${activeGridText}")
                        }
                        Button(
                            modifier = Modifier
                                .width(200.dp),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = Color.LightGray
                            ),
                            onClick = {
                                currentActiveGrid = gridViewModel.currentGrid.toGrid()
                                var scanWifiResult = scanWifi(context)
                                if (scanWifiResult.isNotEmpty()) {
                                    wifiList = scanWifiResult
                                }
                                if (!wifiList.isNullOrEmpty()) {
                                    for (i in wifiList) {
                                        ssidList.add(i.ssid)
                                        dbmList.add(i.dbm)
                                    }
                                }
                                coroutineScope.launch {
                                    if (currentActiveGrid.id == 0) {
                                        currentActiveGrid = gridListDb.gridList[0]
                                    }
                                    if (chosenIdSsid != 0) {
                                        chosenSsid = wifiViewModel.selectWifiById(chosenIdSsid)
                                    }
                                    var dbm = if (ssidList.indexOf(chosenSsid.ssid) != -1) {
                                        dbmList[
                                            ssidList.indexOf(chosenSsid.ssid)
                                        ]
                                    }else{
                                        -100
                                    }
                                    var inputDbm = dbmViewModel.dbmUiState.dbmDetails.copy(
                                        idCollectData = data.id,
                                        idGrid = currentActiveGrid.id,
                                        dbm = dbm
                                    )
                                    if (currentActiveGrid.id !in gridHaveDbm) {
                                        dbmViewModel.saveDbm(inputDbm)
                                        gridHaveDbm.add(currentActiveGrid.id)
                                    }
                                }
                            },
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                dbmObserveInt = observeChosenSsidDbm.getDbm
                                dbmText = if (dbmObserveInt != 0) {
                                    if (dbmObserveInt == -100){
                                        "Tidak terdeteksi"
                                    }else {
                                        "$dbmObserveInt dbm"
                                    }
                                }else{
                                    "Loading..."
                                }
                                Text(
                                    text = dbmText,
                                    fontSize = if (dbmObserveInt != 0){
                                        if (dbmObserveInt != -100) {
                                            30.sp
                                        }else{
                                            15.sp
                                        }
                                    }else{
                                        20.sp
                                    },
                                    color = if (dbmObserveInt == 0){
                                        Color(0xFF000000)
                                    } else if (dbmObserveInt >= -67) {
                                        Color(0xFF1AFF00)
                                    } else if (dbmObserveInt >= -70 && dbmObserveInt <= -68) {
                                        Color(0xFFFFEB3B)
                                    } else if (dbmObserveInt >= -80 && dbmObserveInt <= -71) {
                                        Color(0xFFFF9800)
                                    } else if (dbmObserveInt < -80) {
                                        Color(0xFFFF0000)
                                    } else {
                                        Color(0xFFFF0000)
                                    },
                                )
                                Text(
                                    text = "Pastikan HP dalam posisi stabil dan tidak bergerak",
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp
                                )
                                Text(
                                    modifier = Modifier
                                        .padding(top = 10.dp), text = "Ambil data",
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Row(
                            modifier = Modifier
                                .offset(y = 20.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                modifier = Modifier
                                    .size(70.dp)
                                    .padding(5.dp),
                                shape = RoundedCornerShape(50.dp),
                                onClick = {
                                    var prevAndCurrentGrid = navButtonClick(
                                        gridViewModel,
                                        gridListDb,
                                        data,
                                        idGrids,
                                        firstGridId,
                                        lastGridId,
                                        "up"
                                    )

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
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                modifier = Modifier
                                    .size(70.dp)
                                    .padding(5.dp),
                                shape = RoundedCornerShape(50.dp),
                                onClick = {
                                    var prevAndCurrentGrid = navButtonClick(
                                        gridViewModel,
                                        gridListDb,
                                        data,
                                        idGrids,
                                        firstGridId,
                                        lastGridId,
                                        "left"
                                    )

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
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "left arrow"
                                )
                            }

                            Button(
                                modifier = Modifier
                                    .size(70.dp)
                                    .padding(5.dp),
                                shape = RoundedCornerShape(50.dp),
                                onClick = {
                                    var prevAndCurrentGrid = navButtonClick(
                                        gridViewModel,
                                        gridListDb,
                                        data,
                                        idGrids,
                                        firstGridId,
                                        lastGridId,
                                        "right"
                                    )

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
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "right arrow"
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .offset(y = (-20).dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                modifier = Modifier
                                    .size(70.dp)
                                    .padding(5.dp),
                                shape = RoundedCornerShape(50.dp),
                                onClick = {
                                    var prevAndCurrentGrid = navButtonClick(
                                        gridViewModel,
                                        gridListDb,
                                        data,
                                        idGrids,
                                        firstGridId,
                                        lastGridId,
                                        "down"
                                    )

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
                                Icon(
                                    Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "down arrow"
                                )
                            }
                        }
                    }
                }
                Button(
                    modifier = Modifier
                        .padding(5.dp),
                    shape = RoundedCornerShape(50.dp),
                    enabled = isSaveImageButton,
                    onClick = {
                        coroutineScope.launch {
                            val uri = imageBitmap.asAndroidBitmap().saveToDisk(context)
                            shareBitmap(context, uri)
                        }
                    }
                ) {
                    Text("Simpan gambar peta grid")
                }
            }
        }
    }
    LaunchedEffect(observeChosenSsidDbm) {
        observeChosenSsidDbm.run()
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
    var gridCmToM = data.gridDistance.toFloat().div(100)
    if (direction == "up") {
        if (currentActiveGridPosition - (data.length.toInt() / gridCmToM).toInt() >= 1) {
            isMoveGrid = true
            chosenIdGrid = currentActiveGrid.id - (data.length.toInt() / gridCmToM).toInt()
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
        if (currentActiveGridPosition + (data.length.toInt() / gridCmToM).toInt()  <= lastGridId - firstGridId + 1) {
            isMoveGrid = true
            chosenIdGrid = currentActiveGrid.id + (data.length.toInt() / gridCmToM).toInt()
        }
    }

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

private fun shareBitmap(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(context, createChooser(intent, "Share your image"), null)
}

private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private suspend fun scanFilePath(context: Context, filePath: String): Uri? {
    return suspendCancellableCoroutine { continuation ->
        MediaScannerConnection.scanFile(
            context,
            arrayOf(filePath),
            arrayOf("image/png")
        ) { _, scannedUri ->
            if (scannedUri == null) {
                continuation.cancel(Exception("File $filePath could not be scanned"))
            } else {
                continuation.resume(scannedUri,onCancellation = null)
            }
        }
    }
}

private suspend fun Bitmap.saveToDisk(context: Context): Uri {
    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "screenshot-${System.currentTimeMillis()}.png"
    )

    file.writeBitmap(this, Bitmap.CompressFormat.PNG, 100)

    return scanFilePath(context, file.path) ?: throw Exception("File could not be saved")
}
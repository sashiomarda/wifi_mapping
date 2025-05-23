package com.sashiomarda.wifimapping.components

//noinspection SuspiciousImport
import android.R
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sashiomarda.wifimapping.data.Dbm
import com.sashiomarda.wifimapping.data.Grid
import com.sashiomarda.wifimapping.ui.locateRouter.RouterPosition
import com.sashiomarda.wifimapping.ui.viewmodel.GridViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.toGrid
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.run

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun CanvasGrid(
    length: Float? = 6.0f,
    width: Float? = 4.0f,
    grid: Int? = 100,
    gridViewModel: GridViewModel,
    chosenIdSsid: Int = 0,
    gridListDb: List<Grid>? = null,
    selectedLayer: Int? = null,
    saveIdGridRouterPosition: (Int) -> Unit,
    screen: String,
    addChosenIdList: (Int, Int) -> Unit,
    updateGridList: (Int) -> Unit,
    routerPositions: List<RouterPosition>? = null,
    dbmListDb: List<Dbm>? = null
){
    val coroutineScope = rememberCoroutineScope()
    val localDensity = LocalDensity.current
    var gridCmToM = grid?.toFloat()?.div(100)
    val configuration = LocalConfiguration.current
//    val screenHeight = configuration.screenHeightDp * 0.8
    val screenWidth = configuration.screenWidthDp * 0.86
    var canvasHeight = 0.0
    var canvasWidth = 0.0
    var aspectRatioWidth = 0.2f
    var gridVerticalAmount = 0.0f
    var gridHorizontalAmount = 0.0f
    if (length!! > width!!) {
        aspectRatioWidth = width / length
        canvasHeight = screenWidth
        canvasWidth = screenWidth * aspectRatioWidth
        gridVerticalAmount = length / gridCmToM!!
        gridHorizontalAmount = width / gridCmToM
    }else{
        aspectRatioWidth = length / width
        canvasWidth = screenWidth
        canvasHeight = screenWidth * aspectRatioWidth
        gridVerticalAmount = width / gridCmToM!!
        gridHorizontalAmount = length / gridCmToM
    }
    canvasWidth = ceil(canvasWidth)
    val context = LocalContext.current
    val gridHeight = canvasWidth * gridCmToM / width
    val gridWidth = canvasHeight * gridCmToM / length
    var chosenIdGridRouterPosition by remember { mutableIntStateOf(0) }
    var dbmGridMap = HashMap<Int,Int>()
    var wifiGridLocationIndex: MutableList<Int> = ArrayList()
    var gridList by remember { mutableStateOf(listOf(Grid())) }
    if (!gridListDb.isNullOrEmpty()){
        for (i in 1..gridListDb.size) {
            if (gridListDb.get(i-1).idWifi != 0){
                wifiGridLocationIndex.add(i-1)
            }
        }
        gridList = gridListDb
    }
    if (!dbmListDb.isNullOrEmpty()){
        for (i in dbmListDb) {
            dbmGridMap[i.idGrid] = i.dbm
        }
    }
    val star = ImageBitmap.imageResource(id = R.drawable.star_on)

    Surface(modifier = Modifier
        .padding(start = 5.dp)
        .width((canvasHeight ).dp)
        .height(canvasWidth.dp)
        .border(1.dp, color = Color.Black)
    ) {
        Box(modifier = Modifier
            .background(Color.White)
        ){
            Canvas(modifier = Modifier
                .fillMaxSize(),
            ) {
                if (gridListDb != null) {
                    val gridHeightPx = localDensity.run { gridHeight.dp.toPx() }
                    val gridWidthPx = localDensity.run { gridWidth.dp.toPx() }
                    val canvasQuadrantSize =
                        Size(
                            (gridWidthPx * 1)-5,
                            (gridHeightPx * 1)-5
                        )
                    if (screen == "collect_data" || screen == "download_map"){
                        var repeatX = 0
                        var repeatY = 0
                        if (length > width) {
                            repeatX = gridVerticalAmount.toInt()
                            repeatY = gridHorizontalAmount.toInt()
                        }else{
                            repeatX = gridHorizontalAmount.toInt()
                            repeatY = gridVerticalAmount.toInt()
                        }
                        for (i in gridListDb.indices) {
                            var count = 0
                            repeat(repeatY) {y->
                                repeat(repeatX) {x->
                                    var dbm = dbmGridMap[gridListDb.get(i).id]
                                    if (dbm != null) {
                                        if (count == i) {
                                            drawRect(
                                                color = if (dbm == 0) {
                                                    Color(0xFFFFFFFF)
                                                } else if (dbm >= -67) {
                                                    Color(0xFF1AFF00)
                                                } else if (dbm >= -70 && dbm <= -68) {
                                                    Color(0xFFFFEB3B)
                                                } else if (dbm >= -80 && dbm <= -71) {
                                                    Color(0xFFFF9800)
                                                } else if (dbm < -80) {
                                                    Color(0xFFFF0000)
                                                } else {
                                                    Color(0xFFFF0000)
                                                },
                                                size = canvasQuadrantSize,
                                                topLeft = Offset(
                                                    x = gridWidthPx * x,
                                                    y = gridHeightPx * y
                                                ),
                                            )
                                            drawRect(
                                                color = Color.Black,
                                                topLeft = Offset(
                                                    x = gridWidthPx * x,
                                                    y = gridHeightPx * y
                                                ),
                                                size = canvasQuadrantSize,
                                                style = Stroke(width = 1f)
                                            )
                                            if (gridListDb.get(i).idWifi != 0) {
                                                drawImage(
                                                    image = star,
                                                    topLeft = Offset(
                                                        x = gridWidthPx * x,
                                                        y = gridHeightPx * y
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    count += 1
                                }
                            }
                        }
                    }
                }
            }
        }

        if (gridListDb != null) {
            if (gridListDb.isNotEmpty()) {
                if (selectedLayer != null) {
                    val firstGridID = gridListDb[0].id
                    LazyVerticalGrid(
                        modifier = Modifier,
                        columns = GridCells.Adaptive(floor(gridWidth).dp - ((floor(gridWidth) * 0.01).dp))
                    ) {
                        items(
                            gridList,
                            key = { grid: Grid ->
                                grid.id
                            }) { it ->
                            OutlinedButton(
                                modifier = Modifier
                                    .width(gridWidth.dp)
                                    .height(gridHeight.dp),
                                shape = RectangleShape,
                                onClick = {
                                    if (screen == "locate_router") {
                                        if (chosenIdSsid != 0) {
                                            val foundSsidId =
                                                routerPositions?.firstOrNull { it.ssidId == chosenIdSsid }
                                            if (foundSsidId == null) {
                                                chosenIdGridRouterPosition = it.id
                                                saveIdGridRouterPosition(chosenIdGridRouterPosition)
                                                addChosenIdList(
                                                    chosenIdSsid,
                                                    chosenIdGridRouterPosition
                                                )
                                                coroutineScope.launch {
                                                    gridViewModel.updateUiState(
                                                        gridViewModel.gridUiState.gridDetails.copy(
                                                            id = it.id,
                                                            idRoom = it.idRoom,
                                                            idHistory = it.idHistory,
                                                            idWifi = chosenIdSsid,
                                                            isClicked = it.isClicked,
                                                            layerNo = it.layerNo
                                                        )
                                                    )
                                                    gridViewModel.updateGrid()
                                                    updateGridList(it.layerNo)
                                                }
                                            } else {
                                                val gridTxt = foundSsidId.grid - firstGridID + 1
                                                Toast.makeText(
                                                    context,
                                                    "${foundSsidId.ssid} sudah ditambahkan " +
                                                            "di Layer ${foundSsidId.layer} " +
                                                            "Grid ${gridTxt}",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            }
                                        }
                                    } else if (screen == "collect_data") {
                                        var currentActiveGrid = gridViewModel.currentGrid.toGrid()
                                        if (gridList[0].isClicked) {
                                            currentActiveGrid = gridList[0]
                                        }
                                        coroutineScope.launch {
                                            gridViewModel.updateChosenGrid(
                                                currentActiveGrid.copy(isClicked = false),
                                                it.copy(isClicked = true)
                                            )
                                            updateGridList(it.layerNo)
                                        }
                                    }
                                },
                                border = BorderStroke(
                                    width = if (screen == "collect_data") {
                                        if (it.isClicked) {
                                            3.dp
                                        } else {
                                            1.dp
                                        }
                                    } else {
                                        1.dp
                                    },
                                    color = if (screen == "collect_data") {
                                        if (it.isClicked) {
                                            Color.Blue
                                        } else {
                                            Color.Black
                                        }
                                    } else {
                                        Color.Black
                                    }
                                ),
                                enabled = if (screen != "locate_router") {
                                    true
                                } else {
                                    it.idWifi == 0
                                }
                            ) {
                                Text(
                                    fontSize = 10.sp,
                                    text = "${it.id - firstGridID + 1}"
                                )
                                if (it.idWifi != 0 && screen == "locate_router") {
                                    val image = painterResource(R.drawable.star_on)
                                    Image(painter = image,
                                    contentDescription = "router location")
                                }
                            }
                        }
                    }
                }
            }
        }else{
            var repeatX = 0
            var repeatY = 0
            if (length > width) {
                repeatX = gridHorizontalAmount.toInt()
                repeatY = gridVerticalAmount.toInt()
            }else{
                repeatX = gridVerticalAmount.toInt()
                repeatY = gridHorizontalAmount.toInt()
            }
            Column {
                repeat(repeatX) { i ->
                    Row(
                        modifier = Modifier
                            .height(gridHeight.dp)
                    ) {
                        repeat(repeatY) { j ->
                            OutlinedButton(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(gridWidth.dp),
                                shape = RectangleShape,
                                onClick = {}
                            ) { }
                        }
                    }
                }
            }
        }
    }
}
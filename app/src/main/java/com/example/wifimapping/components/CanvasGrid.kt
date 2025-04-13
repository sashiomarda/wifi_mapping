package com.example.wifimapping.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wifimapping.data.Grid
import com.example.wifimapping.ui.viewmodel.DbmViewModel
import com.example.wifimapping.ui.viewmodel.GridUiStateList
import com.example.wifimapping.ui.viewmodel.GridViewModel
import com.example.wifimapping.ui.viewmodel.toGrid
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.run

@Preview
@Composable
fun CanvasGrid(
    length: Float? = 6.0f,
    width: Float? = 4.0f,
    grid: Int? = 100,
    gridViewModel: GridViewModel,
    chosenIdSsid: Int = 0,
    gridListDb: GridUiStateList? = null,
    saveIdGridRouterPosition: (Int) -> Unit,
    screen: String,
    dbmViewModel: DbmViewModel,
    saveCanvasBitmap: (ImageBitmap) -> Unit
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
        gridHorizontalAmount = width / gridCmToM!!
    }else{
        aspectRatioWidth = length / width
        canvasWidth = screenWidth
        canvasHeight = screenWidth * aspectRatioWidth
        gridVerticalAmount = width / gridCmToM!!
        gridHorizontalAmount = length / gridCmToM!!
    }
    canvasWidth = ceil(canvasWidth)
    val context = LocalContext.current
    val gridHeight = canvasWidth * gridCmToM!! / width
    val gridWidth = canvasHeight * gridCmToM / length
    var chosenIdGridRouterPosition by remember { mutableIntStateOf(0) }

    val dbmListDb by dbmViewModel.dbmUiStateList.collectAsState()
    var dbmGridMap = HashMap<Int,Int>()
    if (dbmListDb.dbmList.isNotEmpty()){
        for (i in dbmListDb.dbmList) {
            dbmGridMap[i.idGrid] = i.dbm
        }
    }
    val graphicsLayer = rememberGraphicsLayer()
    Surface(modifier = Modifier
//        .background(Color.White)
        .padding(start = 5.dp)
        .width((canvasHeight ).dp)
        .height(canvasWidth.dp)
        .border(1.dp, color = Color.Black)
    ) {
        Box(modifier = Modifier
            .background(Color.White)
            .drawWithContent {
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawLayer(graphicsLayer)
                coroutineScope.launch {
                    var canvasBitmap = graphicsLayer.toImageBitmap()
                    if (dbmListDb.dbmList.size == gridListDb?.gridList?.size) {
                        saveCanvasBitmap(canvasBitmap)
                    }
                }
            }
        ){
            Canvas(modifier = Modifier
                .fillMaxSize(),
            ) {
                if (gridListDb != null) {
                    val gridHeightPx = localDensity.run { gridHeight.dp.toPx() }
                    val gridWidthPx = localDensity.run { gridWidth.dp.toPx() }
                    val canvasQuadrantSize =
                        Size(
                            gridHeightPx * 1,
                            gridWidthPx * 1
                        )
                    if (screen == "collect_data"){
                        for (i in gridListDb.gridList.indices) {
                            var count = 0
                            repeat(gridHorizontalAmount.toInt()) {y->
                                repeat(gridVerticalAmount.toInt()) {x->
                                    var dbm = dbmGridMap[gridListDb.gridList[i].id]
                                    if (dbm != null) {
                                        if (count == i) {
                                            drawRect(
                                                color = if (dbm > -70) {
                                                    Color(0xFF1AFF00)
                                                } else if (dbm >= -85 && dbm <= -70) {
                                                    Color(0xFFFFEB3B)
                                                } else if (dbm >= -100 && dbm <= -86) {
                                                    Color(0xFFFF9800)
                                                } else if (dbm < -100) {
                                                    Color(0xFFFF0000)
                                                } else {
                                                    Color(0xFFFF0000)
                                                },
//                                                color = Color.White,
                                                size = canvasQuadrantSize,
                                                topLeft = Offset(
                                                    x = gridWidthPx * x,
                                                    y = gridHeightPx * y
                                                )
                                            )
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
            if (gridListDb.gridList.isNotEmpty()) {
                val firstGridID = gridListDb.gridList[0].id
                LazyVerticalGrid(
                    modifier = Modifier,
                    columns = GridCells.Adaptive(floor(gridWidth).dp-2.dp)
                ) {
                    items(gridListDb.gridList,
                        key = {
                            grid : Grid ->
                            grid.id
                        }) { it ->
                        OutlinedButton(
                            modifier = Modifier
                                .width(gridWidth.dp)
                                .height(gridHeight.dp),
//                            .background(Color(0xFFFF5858)),
                            shape = RectangleShape,
                            onClick = {
                                if (screen == "locate_router") {
                                    if (chosenIdSsid != 0) {
                                        chosenIdGridRouterPosition = it.id
                                        saveIdGridRouterPosition(chosenIdGridRouterPosition)
                                        coroutineScope.launch {
                                            gridViewModel.updateUiState(
                                                gridViewModel.gridUiState.gridDetails.copy(
                                                    id = it.id,
                                                    idCollectData = it.idCollectData,
                                                    idWifi = chosenIdSsid,
                                                    isClicked = it.isClicked
                                                )
                                            )
                                            gridViewModel.updateGrid()
                                        }
                                    }
                                }else if (screen == "collect_data"){
                                    var currentActiveGrid = gridViewModel.currentGrid.toGrid()
                                    if (gridListDb.gridList[0].isClicked){
                                        currentActiveGrid = gridListDb.gridList[0]
                                    }
                                    coroutineScope.launch {
                                        gridViewModel.updateChosenGrid(
                                            currentActiveGrid.copy(isClicked = false),
                                            it.copy(isClicked = true)
                                        )
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
                                }else{
                                    1.dp
                                },
                                color = if (screen == "collect_data") {
                                    if (it.isClicked) {
                                        Color.Blue
                                    }else {
                                        Color.Black
                                    }
                                } else {
                                    Color.Black
                                }
                            )
                        ) {
                            Text(
                                fontSize = 10.sp,
                                text = "${it.id - firstGridID + 1}"
                            )
                            if (it.idWifi != 0) {
                                Icon(Icons.Default.Star, contentDescription = "Wifi Location")
                            }
                        }
                    }
                }
            }
        }else{
            var repeatX = 0
            var repeatY = 0
            if (length!! > width!!) {
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
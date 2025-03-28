package com.example.wifimapping.components

import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wifimapping.ui.viewmodel.GridUiStateList
import com.example.wifimapping.ui.viewmodel.GridViewModel
import kotlinx.coroutines.launch
import kotlin.run
import kotlin.toString

@Preview
@Composable
fun CanvasGrid(
    length: Float? = 6.0f,
    width: Float? = 4.0f,
    grid: Int? = 100,
    gridViewModel: GridViewModel,
    chosenIdSsid: Int = 0,
    gridListDb: GridUiStateList? = null,
){
    val coroutineScope = rememberCoroutineScope()
    val localDensity = LocalDensity.current
    var gridCmToM = grid?.toFloat()?.div(100)
    val configuration = LocalConfiguration.current
//    val screenHeight = configuration.screenHeightDp * 0.8
    val screenWidth = configuration.screenWidthDp * 0.8
    var canvasHeight = 0.0
    var canvasWidth = 0.0
    var aspectRatioWidth = 0.2f
    if (length!! > width!!) {
        aspectRatioWidth = width / length
        canvasHeight = screenWidth
        canvasWidth = screenWidth * aspectRatioWidth
    }else{
        aspectRatioWidth = length / width
        canvasWidth = screenWidth
        canvasHeight = screenWidth * aspectRatioWidth
    }
    val context = LocalContext.current
    val gridHeight = canvasWidth * gridCmToM!! / width
    val gridWidth = canvasHeight * gridCmToM / length
    val gridVerticalAmount = width / gridCmToM
    val gridHorizontalAmount = length / gridCmToM
    var chosenIdWifiRouterPosition by remember { mutableIntStateOf(0) }

    Surface(modifier = Modifier
//        .background(Color.White)
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
                            gridHeightPx * 1,
                            gridWidthPx * 1
                        )
                    for (i in gridListDb.gridList.indices) {
                        repeat(gridHorizontalAmount.toInt()) {y->
                            repeat(gridVerticalAmount.toInt()) {x->
                                drawRect(
//                                    color = if (x == 0) Color.Red else Color.Blue,
                                    color = Color.White,
                                    size = canvasQuadrantSize,
                                    topLeft = Offset(x = gridHeightPx * x, y = gridHeightPx * y)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (gridListDb != null) {
            val firstGridID = gridListDb.gridList[0].id
            LazyVerticalGrid(
                modifier = Modifier,
                columns = GridCells.Adaptive(gridWidth.dp-10.dp)
            ) {
                items(gridListDb.gridList){it->
                    OutlinedButton(
                        modifier = Modifier
                            .width(gridWidth.dp)
                            .height(gridHeight.dp),
//                            .background(Color(0xFFFF5858)),
                        shape = RectangleShape,
                        onClick = {
                            chosenIdWifiRouterPosition = it.id
                            Toast.makeText(context, "${it.id}", Toast.LENGTH_LONG).show()
                            coroutineScope.launch {
                                gridViewModel.updateUiState(
                                    gridViewModel.gridUiState.gridDetails.copy(
                                        id = it.id,
                                        idCollectData = it.idCollectData,
                                        idWifi = chosenIdSsid
                                    )
                                )
                                gridViewModel.updateGrid()
                            }
                        }
                    ) {
                    Text(fontSize = 10.sp,
                        text = "${it.id-firstGridID+1}")
                        if (it.idWifi != 0) {
                            Icon(Icons.Default.Star, contentDescription = "content description")
                        }
                    }
                }
            }
        }else{
            Column {
                repeat(gridVerticalAmount.toInt()) { i ->
                    Row(
                        modifier = Modifier
                            .height(gridHeight.dp)
                    ) {
                        repeat(gridHorizontalAmount.toInt()) { j ->
                            OutlinedButton(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(gridWidth.dp),
                                shape = RectangleShape,
                                onClick = {
                                    Toast.makeText(context, "ok", Toast.LENGTH_LONG)
                                        .show()
                                }
                            ) { }
                        }
                    }
                }
            }
        }
    }
}
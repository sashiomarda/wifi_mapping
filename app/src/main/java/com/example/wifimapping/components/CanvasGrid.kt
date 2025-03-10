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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun CanvasGrid(length: Float?,
               width: Float?,
               grid: Int?){

    val localDensity = LocalDensity.current
    var gridCmToM = grid?.toFloat()?.div(100)
    Log.d("gridCmToM", gridCmToM.toString())
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
    val gridWidth = canvasHeight * gridCmToM!! / length
    val gridVerticalAmount = width / gridCmToM!!
    val gridHorizontalAmount = length / gridCmToM!!
    Log.d("gridHeight", gridHeight.toString())
    Log.d("gridWidth", gridWidth.toString())
    Log.d("gridVerticalAmount", gridVerticalAmount.toString())
    Log.d("gridHorizontalAmount", gridHorizontalAmount.toString())
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
                repeat(10) {  i -> //To-do change the value 10
                    repeat(5) { j -> //To-do change the value 5
                        val gridHeightPx = localDensity.run { gridHeight.dp.toPx() }
                        val gridWidthPx = localDensity.run { gridWidth.dp.toPx() }
                        val canvasQuadrantSize =
                            _root_ide_package_.androidx.compose.ui.geometry.Size(
                                gridHeightPx * i,
                                gridWidthPx * j
                            )
                        drawRect(
                            color = Color.White,
                            size = canvasQuadrantSize
                        )
                        Log.d("canvasWidth", canvasWidth.toString())
                        Log.d("canvasHeight", canvasHeight.toString())
                    }
                }
            }
        }

        Column {
            repeat(gridVerticalAmount.toInt()){i ->
                Row(modifier = Modifier
                    .height(gridHeight.dp)
                ) {
                    repeat(gridHorizontalAmount.toInt()){j ->
                        OutlinedButton(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(gridWidth.dp),
                            shape = RectangleShape,
                            onClick = {

                                Toast.makeText(context, "ok", Toast.LENGTH_LONG).show()
                            }
                        ) { }
                    }
                }
            }
        }
    }
}
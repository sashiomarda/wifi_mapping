package com.example.wifimapping.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun CanvasGrid(length: Float?,
               width: Float?,
               grid: Int?){
    Box(modifier = Modifier
        .background(Color.White)
        .padding(start = 5.dp)){
        Canvas(modifier = Modifier
            .padding(8.dp)
            .aspectRatio(length!! / width!!)
            .fillMaxSize(),
        ) {
            val barWidthPx = 1.dp.toPx()
            drawRect(
                Color.Black,
                size = size,
                style = Stroke(barWidthPx)
            )
            val verticalLines = length.toInt()!! / grid!!
            var verticalSize = size.width / verticalLines + 1
            repeat(verticalLines){i ->
                val startX = verticalSize * (i + 1)
                drawLine(
                    Color.Black,
                    start = Offset(startX,0f),
                    end = Offset(startX, size.height),
                    strokeWidth = barWidthPx
                )
            }
            val horizontalLines = (width.toInt()!! / grid!!) - 1
            val sectionSize = size.height/(horizontalLines + 1)
            repeat(horizontalLines){i->
                val startY = sectionSize * (i + 1)
                drawLine(
                    Color.Black,
                    start = Offset(0f, startY),
                    end = Offset(size.width, startY),
                    strokeWidth = barWidthPx
                )
            }
        }
    }
}
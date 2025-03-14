package com.example.wifimapping.screens.previewGrid


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wifimapping.MainActivity
import com.example.wifimapping.components.CanvasGrid
import com.example.wifimapping.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewGridScreen(navController: NavController, inputData: List<String?>, context: MainActivity){
    Scaffold(topBar = {
        TopAppBar(title = { Text("Pendeteksi SSID WiFi", color = Color.Black) },
            colors = TopAppBarDefaults.topAppBarColors(Color(0xFFFFFFFF)
            )
        )
    }) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Spacer(
                modifier = Modifier
                    .height(10.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Preview Grid",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(bottom = 15.dp))
                Text("Panjang ${inputData[0]} m")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier
                        .padding(start = 5.dp)) {
                        Text("Lebar")
                        Text("${inputData[1]} m")
                    }
                    CanvasGrid(length = inputData[0]?.toFloat(),
                        width = inputData[1]?.toFloat(),
                        grid = inputData[2]?.toInt())
                    }
                Button(shape = RoundedCornerShape(5.dp),
                    onClick = {
                        var length = inputData[0]
                        var width = inputData[1]
                        var grid = inputData[2]
                        navController.navigate(route = Screens.ChooseWifiScreen.name+"/$length/$width/$grid"
                        )
                    }) {
                    Text("Selanjutnya")
                }

            }
        }
    }
}
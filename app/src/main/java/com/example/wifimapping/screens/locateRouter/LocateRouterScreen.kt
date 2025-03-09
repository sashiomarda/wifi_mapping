package com.example.wifimapping.screens.locateRouter

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocateRouterScreen(
    navController: NavController,
    inputData: List<String?>,
    context: MainActivity
){
    Scaffold(topBar = {
        TopAppBar(title = { Text("Pendeteksi SSID WiFi", color = Color.Black) },
            colors = TopAppBarDefaults.topAppBarColors(
                Color(0xFFFFFFFF)
            )
        )
    }) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Text("Locate Router Position",
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
                        Toast.makeText(context, "Fitur masih didevelop", Toast.LENGTH_LONG).show()
                    }) {
                    Text("Tambah Lokasi Router")
                }

                Button(shape = RoundedCornerShape(5.dp),
                    onClick = {
                        Toast.makeText(context, "Fitur masih didevelop", Toast.LENGTH_LONG).show()
                    }) {
                    Text("Selanjutnya")
                }
            }
        }
    }
}
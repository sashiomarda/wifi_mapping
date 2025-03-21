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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wifimapping.R
import com.example.wifimapping.components.CanvasGrid
import com.example.wifimapping.ui.AppViewModelProvider
import com.example.wifimapping.ui.navigation.NavigationDestination
import com.example.wifimapping.ui.previewGrid.PreviewGridViewModel

object LocateRouterDestination : NavigationDestination {
    override val route = "locate_router"
    override val titleRes = R.string.locate_router_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocateRouterScreen(
    viewModel: PreviewGridViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val context = LocalContext.current
    var data = viewModel.roomParamsUiState.roomParamsDetails
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

                if (data.length != "") {
                    Text("Panjang ${data.length} m")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(
                            modifier = Modifier
                                .padding(start = 5.dp)
                        ) {
                            Text("Lebar")
                            Text("${data.width} m")
                        }
                        CanvasGrid(
                            length = data.length?.toFloat(),
                            width = data.width?.toFloat(),
                            grid = data.gridDistance?.toInt()
                        )
                    }
                    Button(
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            Toast.makeText(context, "Fitur masih didevelop", Toast.LENGTH_LONG)
                                .show()
                        }) {
                        Text("Tambah Lokasi Router")
                    }

                    Button(
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            Toast.makeText(context, "Fitur masih didevelop", Toast.LENGTH_LONG)
                                .show()
                        }) {
                        Text("Selanjutnya")
                    }
                }
            }
        }
    }
}
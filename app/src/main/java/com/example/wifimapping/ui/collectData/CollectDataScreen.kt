package com.example.wifimapping.ui.collectData

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wifimapping.InventoryTopAppBar
import com.example.wifimapping.R
import com.example.wifimapping.components.CanvasGrid
import com.example.wifimapping.ui.AppViewModelProvider
import com.example.wifimapping.ui.home.ItemEntryDestination
import com.example.wifimapping.ui.navigation.NavigationDestination
import com.example.wifimapping.ui.viewmodel.GridViewModel
import com.example.wifimapping.ui.viewmodel.PreviewGridViewModel
import com.example.wifimapping.ui.viewmodel.RoomParamsEntryViewModel
import kotlinx.coroutines.launch

object CollectDataDestination : NavigationDestination {
    override val route = "collect_data"
    override val titleRes = R.string.collect_data_title
    const val idCollectData = "idCollectData"
    val routeWithArgs = "${route}/{$idCollectData}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectDataScreen(
//    navigateToPreviewGrid: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = false,
    gridViewModel: GridViewModel = viewModel(factory = AppViewModelProvider.Factory),
    previewGridViewModel: PreviewGridViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    var data = previewGridViewModel.roomParamsUiState.roomParamsDetails
    var chosenIdSsid by remember { mutableStateOf(0) }
    val gridListDb by gridViewModel.gridUiStateList.collectAsState()
    var currentActiveGridPosition by remember { mutableStateOf(1) }
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
                                onClickActiveGridPosition = {it ->
                                    currentActiveGridPosition = it
                                },
                                resetGridClicked = {idGrid ->
                                    coroutineScope.launch {
                                        for (it in gridListDb.gridList) {
                                            if (it.id != idGrid) {
                                                gridViewModel.updateUiState(
                                                    gridViewModel.gridUiState.gridDetails.copy(
                                                        id = it.id,
                                                        idCollectData = it.idCollectData,
                                                        idWifi = it.idWifi,
                                                        isClicked = false
                                                    )
                                                )
                                                gridViewModel.updateGrid()
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier
                    .height(30.dp))
                Row {
                    Text("Posisi grid aktif: ",
                        fontSize = 20.sp)
                    Text(
                        "$currentActiveGridPosition",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier
                    .height(30.dp))
                Button(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(5.dp),
                    shape = RoundedCornerShape(50.dp),
                    onClick = {}
                ) {
                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "up arrow")
                }
                Row(modifier = Modifier
                    .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(5.dp),
                        shape = RoundedCornerShape(50.dp),
                        onClick = {}
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "left arrow")
                    }
                    Button(
                        modifier = Modifier
                            .size(150.dp)
                            .padding(5.dp),
                        shape = RoundedCornerShape(50.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color.LightGray
                        ),
                        onClick = {}
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Pastikan HP dalam posisi stabil dan tidak bergerak",
                                textAlign = TextAlign.Center,
                                        fontSize = 12.sp)
                            Text(modifier = Modifier
                                .padding(top = 10.dp), text = "27 s",
                                fontSize = 30.sp)
                        }
                    }
                    Button(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(5.dp),
                        shape = RoundedCornerShape(50.dp),
                        onClick = {}
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "right arrow")
                    }
                }
                Button(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(5.dp),
                    shape = RoundedCornerShape(50.dp),
                    onClick = {}
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
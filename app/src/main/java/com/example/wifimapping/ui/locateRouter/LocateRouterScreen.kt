package com.example.wifimapping.screens.locateRouter

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wifimapping.InventoryTopAppBar
import com.example.wifimapping.R
import com.example.wifimapping.components.CanvasGrid
import com.example.wifimapping.data.Wifi
import com.example.wifimapping.ui.AppViewModelProvider
import com.example.wifimapping.ui.navigation.NavigationDestination
import com.example.wifimapping.ui.viewmodel.PreviewGridViewModel
import com.example.wifimapping.ui.viewmodel.WifiViewModel
import kotlin.Boolean

object LocateRouterDestination : NavigationDestination {
    override val route = "locate_router"
    override val titleRes = R.string.locate_router_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocateRouterScreen(
    navigateToCollectData: () -> Unit,
    previewGridViewModel: PreviewGridViewModel = viewModel(factory = AppViewModelProvider.Factory),
    wifiViewModel: WifiViewModel = viewModel(factory = AppViewModelProvider.Factory),
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
){
    val wifiCheckedUiStateList by wifiViewModel.wifiCheckedUiStateList.collectAsState()
    val context = LocalContext.current
    var data = previewGridViewModel.roomParamsUiState.roomParamsDetails
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(LocateRouterDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
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
                Box(modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp)) {
                    WifiCheckedList(
                        wifiCheckListDb = wifiCheckedUiStateList.wifiList
                    )
                }
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
                        Text("Selanjutnya")
                    }
                }
            }
        }
    }
}

@Composable
fun WifiCheckedList(
    wifiCheckListDb: List<Wifi>
){
    var isChosenIdSSid by remember { mutableStateOf(0) }
    LazyColumn(
        modifier = Modifier
            .padding(10.dp)
    ) {
        Log.d("wifiCheckListDb",wifiCheckListDb.toString())
        Log.d("wifiCheckListDb",isChosenIdSSid.toString())
        items(items = wifiCheckListDb) {
            Surface(
                color = if (isChosenIdSSid == it.id) Color(0xFF464646) else Color.Transparent,
                ) {
                Column {
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clickable {
                                isChosenIdSSid = it.id
                            },
                        colors = CardDefaults.cardColors(Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                        ) {
                            Text(
                                text = "${it.ssid} (Strength: ${it.dbm} dBm)",
                                fontWeight = if (isChosenIdSSid == it.id) FontWeight.Bold else FontWeight.Light,
                            )
                        }
                    }
                    HorizontalDivider(
                        color = Color.LightGray,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                    )
                }
            }
        }
    }
}
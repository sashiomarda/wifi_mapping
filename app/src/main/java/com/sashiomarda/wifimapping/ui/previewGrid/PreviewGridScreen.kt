package com.sashiomarda.wifimapping.ui.previewGrid

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sashiomarda.wifimapping.WifiMappingTopAppBar
import com.sashiomarda.wifimapping.R
import com.sashiomarda.wifimapping.components.CanvasGrid
import com.sashiomarda.wifimapping.ui.AppViewModelProvider
import com.sashiomarda.wifimapping.ui.itemEntry.ItemEntryDestination
import com.sashiomarda.wifimapping.ui.navigation.NavigationDestination
import com.sashiomarda.wifimapping.ui.viewmodel.DbmViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.GridViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.RoomParamsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object PreviewGridDestination : NavigationDestination {
    override val route = "preview_grid"
    override val titleRes = R.string.preview_grid_title
    const val idHistory = "idHistory"
    val routeWithArgs = "${route}/{$idHistory}"
}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewGridScreen(
    navigateToChooseWifi: (Int?) -> Unit,
    canNavigateBack: Boolean = false,
    previewGridviewModel: RoomParamsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    gridViewModel: GridViewModel = viewModel(factory = AppViewModelProvider.Factory),
    dbmViewModel: DbmViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val coroutineScope = rememberCoroutineScope()
    var lastHistoryId: Int? by remember { mutableStateOf(0) }
    val gridListDb by gridViewModel.gridUiStateList.collectAsState()

    Scaffold(
        topBar = {
            WifiMappingTopAppBar(
                title = stringResource(ItemEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
            )
        }
    ) { innerPadding ->
        var data = previewGridviewModel.roomParamByIdsUiState.roomParamsDetails
        gridViewModel.updateUiState(
            gridViewModel.gridUiState.gridDetails.copy(idRoom = data.id)
        )
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
                if (data.length != "") {
                    Text("Panjang ${data.length} m")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(
                            modifier = Modifier
                        ) {
                            Text(
                                modifier = Modifier
                                    .vertical()
                                    .rotate(-90f),
                                text = "Lebar ${data.width} m"
                            )
                        }
                        CanvasGrid(
                            length = data.length.toFloat(),
                            width = data.width.toFloat(),
                            grid = data.gridDistance.toInt(),
                            gridViewModel = gridViewModel,
                            saveIdGridRouterPosition = {},
                            screen = PreviewGridDestination.route,
                            dbmViewModel = dbmViewModel,
                            saveCanvasBitmap = {},
                            addChosenIdList = {ssidId, gridId ->}
                        )
                    }
                    Button(
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            coroutineScope.launch(Dispatchers.Main) {
                                lastHistoryId = gridViewModel.getIdHistory()
                                var gridCmToM = data.gridDistance.toFloat().div(100)
                                val gridCount = (data.length.toInt() / gridCmToM) * (data.width.toInt() / gridCmToM)
                                if (gridListDb.gridList.isEmpty()) {
                                    for (i in 1..gridCount.toInt()) {
                                        var inputGrid = gridViewModel
                                            .gridUiState
                                            .gridDetails
                                            .copy(idHistory = lastHistoryId!!)
                                        if (i == 1) {
                                            inputGrid = gridViewModel
                                                .gridUiState
                                                .gridDetails
                                                .copy(
                                                    idHistory = lastHistoryId!!
                                                )
                                        }
                                        gridViewModel.saveGrid(inputGrid)
                                    }
                                }
                                navigateToChooseWifi(lastHistoryId)
                            }

                        }
                    ) {
                        Text("Selanjutnya")
                    }
                }
            }
        }
    }
}

fun Modifier.vertical() =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            )
        }
    }
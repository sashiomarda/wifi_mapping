package com.example.wifimapping.ui.previewGrid

import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wifimapping.InventoryTopAppBar
import com.example.wifimapping.R
import com.example.wifimapping.components.CanvasGrid
import com.example.wifimapping.ui.AppViewModelProvider
import com.example.wifimapping.ui.home.ItemEntryDestination
import com.example.wifimapping.ui.navigation.NavigationDestination
import com.example.wifimapping.ui.viewmodel.GridViewModel
import com.example.wifimapping.ui.viewmodel.PreviewGridViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object PreviewGridDestination : NavigationDestination {
    override val route = "preview_grid"
    override val titleRes = R.string.preview_grid_title
    const val idCollectData = "idCollectData"
    val routeWithArgs = "${route}/{$idCollectData}"
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewGridScreen(
    navigateToChooseWifi: () -> Unit,
    canNavigateBack: Boolean = false,
    previewGridviewModel: PreviewGridViewModel = viewModel(factory = AppViewModelProvider.Factory),
    gridViewModel: GridViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val coroutineScope = rememberCoroutineScope()
    var lastInputGridId: Int? by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(ItemEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
            )
        }
    ) { innerPadding ->
        var data = previewGridviewModel.roomParamsUiState.roomParamsDetails
        gridViewModel.updateUiState(
            gridViewModel.gridUiState.gridDetails.copy(idCollectData = data.id)
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
                if (data.length != ""){
                    Text("Panjang ${data.length} m")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier
                        .padding(start = 5.dp)) {
                        Text("Lebar")
                        Text("${data.width} m")
                    }
                    CanvasGrid(
                        length = data.length.toFloat(),
                        width = data.width.toFloat(),
                        grid = data.gridDistance.toInt(),
                        gridViewModel = gridViewModel,
                        gridListDb = null,
                    )
                    }
                Button(shape = RoundedCornerShape(5.dp),
                    onClick = {
                        coroutineScope.launch(Dispatchers.Main) {
                            lastInputGridId = gridViewModel.lastGridInputId()?.idCollectData
                            val gridCount = data.length.toInt() * data.width.toInt()
                            if (lastInputGridId != data.id) {
                                repeat(gridCount) {
                                    Log.d(
                                        "saveGrid",
                                        gridViewModel.gridUiState.gridDetails.toString()
                                    )
                                    gridViewModel.saveGrid()
                                }
                            }
                            navigateToChooseWifi()
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
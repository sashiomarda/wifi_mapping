package com.example.wifimapping.ui.previewGrid

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
import com.example.wifimapping.ui.viewmodel.PreviewGridViewModel

object PreviewGridDestination : NavigationDestination {
    override val route = "preview_grid"
    override val titleRes = R.string.preview_grid_title
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewGridScreen(
    navigateToChooseWifi: () -> Unit,
    canNavigateBack: Boolean = false,
    viewModel: PreviewGridViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(ItemEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
            )
        }
    ) { innerPadding ->
        var data = viewModel.roomParamsUiState.roomParamsDetails
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
                    CanvasGrid(length = data.length.toFloat(),
                        width = data.width.toFloat(),
                        grid = data.gridDistance.toInt()
                    )
                    }
                Button(shape = RoundedCornerShape(5.dp),
                    onClick = navigateToChooseWifi
                ) {
                    Text("Selanjutnya")
                }
}
            }
        }
    }
}
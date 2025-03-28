package com.example.wifimapping.ui.collectData

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wifimapping.InventoryTopAppBar
import com.example.wifimapping.R
import com.example.wifimapping.ui.AppViewModelProvider
import com.example.wifimapping.ui.home.ItemEntryDestination
import com.example.wifimapping.ui.navigation.NavigationDestination
import com.example.wifimapping.ui.viewmodel.RoomParamsEntryViewModel

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
    viewModel: RoomParamsEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
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
            .padding(innerPadding)) {
            Text(CollectDataDestination.titleRes.toString())
        }
    }
}
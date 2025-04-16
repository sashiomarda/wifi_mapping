package com.example.wifimapping.ui.history

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.wifimapping.WifiMappingTopAppBar
import com.example.wifimapping.R
import com.example.wifimapping.ui.itemEntry.ItemEntryDestination
import com.example.wifimapping.ui.navigation.NavigationDestination

object HistoryDestination : NavigationDestination {
    override val route = "history"
    override val titleRes = R.string.locate_router_title
    const val idCollectData = "idCollectData"
    val routeWithArgs = "${route}/{$idCollectData}"
}

@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = false,
) {
    Scaffold(
        topBar = {
            WifiMappingTopAppBar(
                title = stringResource(ItemEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Surface(modifier = Modifier
            .padding(innerPadding)
        ) {
            Text("History")
        }
    }
}
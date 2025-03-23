package com.example.wifimapping.ui.chooseWifi

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wifimapping.InventoryTopAppBar
import com.example.wifimapping.R
import com.example.wifimapping.data.Wifi
import com.example.wifimapping.ui.AppViewModelProvider
import com.example.wifimapping.ui.home.ItemEntryDestination
import com.example.wifimapping.ui.navigation.NavigationDestination
import com.example.wifimapping.ui.viewmodel.WifiDetails
import com.example.wifimapping.ui.viewmodel.WifiUiState
import com.example.wifimapping.ui.viewmodel.WifiViewModel
import kotlinx.coroutines.launch
import kotlin.toString
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlin.concurrent.thread

object ChooseWifiDestination : NavigationDestination {
    override val route = "choose_wifi"
    override val titleRes = R.string.choose_wifi_title
    const val ssidArg = "ssid"
}

val PERMISSIONS_REQUEST_CODE = 1
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseWifiScreen(
    canNavigateBack: Boolean = false,
    navigateToLocateRouter: () -> Unit,
    viewModel: WifiViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val wifiUiStateList by viewModel.wifiUiStateList.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(ItemEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
            )
        }
    ) { innerPadding ->
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
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Choose Wifi",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(bottom = 15.dp))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp)) {
                    ScanWifi(
                        wifiListDb = wifiUiStateList.wifiList,
                        context = LocalContext.current,
                        wifiUiState = viewModel.wifiUiState,
                        addUpdateWifi = viewModel::updateUiState,
                        insertWifi = {
                            coroutineScope.launch {
                                viewModel.saveWifi()
                            }
                        },
                        updateWifi = {
                            coroutineScope.launch {
                                viewModel.updateWifi()
                            }
                        }
                    )
                }
                Button(shape = RoundedCornerShape(5.dp),
                    onClick = navigateToLocateRouter
                ) {
                    Text("Selanjutnya")
                }
            }
        }
    }
}

//@SuppressLint("MissingPermission")
//@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ScanWifi(
    wifiListDb: List<Wifi>,
    context: Context,
    wifiUiState: WifiUiState,
    addUpdateWifi: (WifiDetails) -> Unit,
    insertWifi: () -> Unit,
    updateWifi: () -> Unit,
){
    var ssidListDb : MutableList<String> = ArrayList()
    var idListDb : MutableList<Int> = ArrayList()
    for (wifi in wifiListDb){
        ssidListDb.add(wifi.ssid)
        idListDb.add(wifi.id)
    }

    val wifiManager: WifiManager  = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val activity = context as Activity

    val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                scanSuccess(wifiManager,context, activity)
            } else {
                scanFailure(wifiManager,context, activity)
            }
        }
    }

    val intentFilter = IntentFilter()
    intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
    context.applicationContext.registerReceiver(wifiScanReceiver, intentFilter)
    if (wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLED) {
        var results = wifiManager.scanResults
        thread(start = true) {
            results = wifiManager.scanResults
        }
        if (!results.isNullOrEmpty()) {
            var wifiListDisplay: MutableList<Wifi> = ArrayList()
            if (ssidListDb.isNotEmpty()) {
                for (wifi in results) {
                    val wifiSsid = wifi.wifiSsid.toString().removeSurrounding("\"")
                    if (wifiSsid != "") {
                        val isSsidNotExist = !ssidListDb.contains(wifiSsid)
                        if (isSsidNotExist) {
                            addUpdateWifi(wifiUiState.wifiDetails.copy(ssid = wifiSsid))
                            insertWifi()
                        } else {
                            if (wifiListDisplay.isNotEmpty()) {
                                var ssidListDisplay: MutableList<String> = ArrayList()
                                var idListDisplay: MutableList<Int> = ArrayList()
                                for (wifiDisplay in wifiListDisplay) {
                                    ssidListDisplay.add(wifiDisplay.ssid)
                                    idListDisplay.add(wifiDisplay.id)
                                }
                                if (!ssidListDisplay.contains(wifiSsid)) {
                                    wifiListDisplay.add(
                                        Wifi(
                                            id = idListDb[ssidListDb.indexOf(wifiSsid)],
                                            ssid = wifiSsid,
                                            isChecked = false,
                                            dbm = wifi.level
                                        )
                                    )
                                }
                            } else {
                                wifiListDisplay.add(
                                    Wifi(
                                        id = idListDb[ssidListDb.indexOf(wifiSsid)],
                                        ssid = wifiSsid,
                                        isChecked = false,
                                        dbm = wifi.level
                                    )
                                )
                            }

                        }
                    }
                }
            } else {
                addUpdateWifi(wifiUiState.wifiDetails.copy(ssid = "testwifimapping"))
                insertWifi()
            }

            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .height(500.dp),
                shape = RoundedCornerShape(corner = CornerSize(16.dp))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    items(items = wifiListDisplay) {
                        var isChecked by remember { mutableStateOf(false) }
                        var ssid by remember { mutableStateOf(it.ssid) }
                        var ssidId by remember { mutableStateOf(it.id) }
                        Card(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                                .clickable {
                                    isChecked = !isChecked
                                    addUpdateWifi(wifiUiState
                                        .wifiDetails.copy(
                                            id = ssidId,
                                            ssid = ssid,
                                            isChecked = isChecked
                                        ))
                                    updateWifi()
                                }) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Column {
                                    if (ssid != "") {
                                        Text(ssid)
                                    }
                                    Text("Strength: ${it.dbm} dBm")
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    if (ssid != "") {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = {
                                                isChecked = it
                                                addUpdateWifi(wifiUiState
                                                    .wifiDetails.copy(
                                                        id = ssidId,
                                                        ssid = ssid,
                                                        isChecked = isChecked
                                                    ))
                                                updateWifi()
                                            }
                                        )
                                    }
                                }
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
        }else{
            Text("Tidak ada sinyal wifi di sini")
        }
    }
}

private fun scanSuccess(wifiManager: WifiManager, context: Context, activity: Activity) {
    val results = if (checkSelfPermission(
            context.applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        requestPermissions(activity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE
            ),
            PERMISSIONS_REQUEST_CODE);
        return
    } else {
        wifiManager.scanResults
    }
}

private fun scanFailure(wifiManager: WifiManager, context: Context, activity: Activity) {
    val results = if (checkSelfPermission(
            context.applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        requestPermissions(activity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE
            ),
            PERMISSIONS_REQUEST_CODE);
        return
    } else {
        wifiManager.scanResults
    }
}

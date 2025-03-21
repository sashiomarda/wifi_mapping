package com.example.wifimapping.ui.chooseWifi

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.wifimapping.InventoryTopAppBar
import com.example.wifimapping.R
import com.example.wifimapping.ui.home.ItemEntryDestination
import com.example.wifimapping.ui.navigation.NavigationDestination
import kotlin.toString

object ChooseWifiDestination : NavigationDestination {
    override val route = "choose_wifi"
    override val titleRes = R.string.choose_wifi_title
}

val PERMISSIONS_REQUEST_CODE = 1
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseWifiScreen(
    canNavigateBack: Boolean = false,
    navigateToLocateRouter: () -> Unit,
){
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
                    ScanWifi(LocalContext.current){ssid ->
                        Log.d("ChooseWifiScreen", ssid)
                    }
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

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ScanWifi(context: Context, onItemClick: (String) -> Unit = {}){
    val wifiManager: WifiManager  = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val activity = context as Activity

    val wifiScanReceiver = object : BroadcastReceiver() {
//        @RequiresApi(Build.VERSION_CODES.M)
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
        val results = wifiManager.scanResults
        Log.d("scanwifi",results.toString())
        Card(modifier = Modifier
            .padding(10.dp)
            .height(500.dp),
            shape = RoundedCornerShape(corner = CornerSize(16.dp))
        ) {
            LazyColumn(modifier = Modifier
                .padding(10.dp)) {
                items(items = results) {
                    var isChecked by remember { mutableStateOf(false) }
                    Card(modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .clickable {
                            onItemClick(it.wifiSsid.toString())
                            isChecked = !isChecked
                        }) {
                        Row(modifier = Modifier
                            .fillMaxWidth()) {
                            Column {
                                if (it.wifiSsid.toString() == ""){
                                    Text("Hidden SSID")
                                }else {
                                    Text(it.wifiSsid.toString())
                                }
                                Text("Strength: ${it.level} dBm")
                            }
                            Column(modifier = Modifier
                                .fillMaxWidth(),
                                horizontalAlignment = Alignment.End
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { isChecked = it }
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = Color.LightGray,
                        modifier = Modifier
                            .padding(bottom = 10.dp))
                }
            }
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

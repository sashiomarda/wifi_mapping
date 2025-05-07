package com.sashiomarda.wifimapping.ui.chooseWifi

import android.annotation.SuppressLint
import android.os.Build
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sashiomarda.wifimapping.WifiMappingTopAppBar
import com.sashiomarda.wifimapping.R
import com.sashiomarda.wifimapping.data.Wifi
import com.sashiomarda.wifimapping.ui.AppViewModelProvider
import com.sashiomarda.wifimapping.ui.roomInput.RoomInputDestination
import com.sashiomarda.wifimapping.ui.navigation.NavigationDestination
import com.sashiomarda.wifimapping.ui.viewmodel.WifiDetails
import com.sashiomarda.wifimapping.ui.viewmodel.WifiUiState
import com.sashiomarda.wifimapping.ui.viewmodel.WifiViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.sashiomarda.wifimapping.ui.viewmodel.RoomParamsViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.WifiScan
import com.sashiomarda.wifimapping.ui.viewmodel.WifiScannerViewModel
import kotlinx.coroutines.*

object ChooseWifiDestination : NavigationDestination {
    override val route = "choose_wifi"
    override val titleRes = R.string.choose_wifi_title
    const val idHistory = "idHistory"
    val routeWithArgs = "${route}/{$idHistory}"
}

const val PERMISSIONS_REQUEST_CODE = 1
@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseWifiScreen(
    canNavigateBack: Boolean = false,
    navigateToLocateRouter: (Int) -> Unit,
    wifiViewModel: WifiViewModel = viewModel(factory = AppViewModelProvider.Factory),
    previewGridviewModel: RoomParamsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    wifiScannerViewModel: WifiScannerViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val wifiUiStateList by wifiViewModel.allWifiUiStateList.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        wifiScannerViewModel.startScanning()
    }
    val wifiScanList by wifiScannerViewModel.ssidDbms.collectAsState()
    wifiScannerViewModel.updateScreen("choose_wifi")
    var isNextButtonDisabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            WifiMappingTopAppBar(
                title = stringResource(RoomInputDestination.titleRes),
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
            Column(modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Pilih Wifi",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(bottom = 15.dp))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp)) {
                    WifiList(
                        wifiScanList = wifiScanList,
                        wifiListDb = wifiUiStateList.wifiList,
                        wifiUiState = wifiViewModel.wifiUiState,
                        addUpdateWifi = wifiViewModel::updateUiState,
                        insertWifi = {
                            coroutineScope.launch {
                                wifiViewModel.saveWifi()
                            }
                        },
                        updateWifi = {
                            coroutineScope.launch {
                                wifiViewModel.updateWifi()
                            }
                        },
                        isNextButtonDisabled = {
                            isNextButtonDisabled = it
                        },
                        resetWifiChecked = {
                            CoroutineScope(Dispatchers.IO).launch {
                                wifiViewModel.resetCheckedWifi()
                            }
                        },
                        wifiScannerViewModel = wifiScannerViewModel
                    )
                }
                Row {
                    Button(
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier
                            .padding(1.dp)
                            .padding(5.dp),
                        onClick = {
                            wifiScannerViewModel.updateIsRefreshWifiScan(true)
                        }
                    ) {
                        Text("Refresh Wifi")
                    }
                    Button(
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            var data = previewGridviewModel.historyByIdUiState.historyDetails
                            navigateToLocateRouter(data.id)
                        },
                        enabled = !isNextButtonDisabled,
                        modifier = Modifier
                            .padding(1.dp)
                            .padding(5.dp)
                    ) {
                        Text("Selanjutnya")
                    }
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            wifiViewModel.resetCheckedWifi()
        }
    }
}

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun WifiList(
    wifiScanList: List<WifiScan>,
    wifiListDb: List<Wifi>,
    wifiUiState: WifiUiState,
    addUpdateWifi: (WifiDetails) -> Unit,
    insertWifi: () -> Unit,
    updateWifi: () -> Unit,
    isNextButtonDisabled: (Boolean) -> Unit,
    resetWifiChecked: () -> Unit,
    wifiScannerViewModel: WifiScannerViewModel,
){
    var wifiListDisplay : MutableList<Wifi> = ArrayList()
    var ssidListDb : MutableList<String> = ArrayList()
    var idListDb : MutableList<Int> = ArrayList()
    for (wifi in wifiListDb){
        ssidListDb.add(wifi.ssid)
        idListDb.add(wifi.id)
    }
    var isResetWifiChecked by remember { mutableStateOf(true) }
    var checkedCount by remember { mutableIntStateOf(0) }
    if (isResetWifiChecked){
        resetWifiChecked()
        isResetWifiChecked = false
    }
    if (wifiScanList.isNotEmpty()) {
        if (ssidListDb.isNotEmpty()) {
            for (i in wifiScanList.indices) {
                val wifiSsid = wifiScanList[i].ssid
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
                                wifiListDisplay.add(Wifi(
                                        id = idListDb[ssidListDb.indexOf(wifiSsid)],
                                        ssid = wifiSsid,
                                        isChecked = false,
                                        dbm = wifiScanList[i].dbm
                                    )
                                )
                            }
                        } else {
                            wifiListDisplay.add(
                                Wifi(
                                    id = idListDb[ssidListDb.indexOf(wifiSsid)],
                                    ssid = wifiSsid,
                                    isChecked = false,
                                    dbm = wifiScanList[i].dbm
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
                items(
                    items = wifiListDisplay,
                    key = { wifi: Wifi ->
                        wifi.id
                    }) {
                    var isChecked by remember { mutableStateOf(false) }
                    var ssid by remember { mutableStateOf(it.ssid) }
                    var ssidId by remember { mutableIntStateOf(it.id) }
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clickable {
                                isChecked = !isChecked
                                if (isChecked) {
                                    checkedCount = checkedCount + 1
                                } else {
                                    if (checkedCount > 0) {
                                        checkedCount = checkedCount - 1
                                    }
                                }
                                if (checkedCount > 0) {
                                    isNextButtonDisabled(false)
                                } else {
                                    isNextButtonDisabled(true)
                                }

                                addUpdateWifi(
                                    wifiUiState
                                        .wifiDetails.copy(
                                            id = ssidId,
                                            ssid = ssid,
                                            isChecked = isChecked
                                        )
                                )
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
                                            if (isChecked) {
                                                checkedCount = checkedCount + 1
                                            } else {
                                                if (checkedCount > 0) {
                                                    checkedCount = checkedCount - 1
                                                }
                                            }
                                            if (checkedCount > 0) {
                                                isNextButtonDisabled(false)
                                            } else {
                                                isNextButtonDisabled(true)
                                            }
                                            addUpdateWifi(
                                                wifiUiState
                                                    .wifiDetails.copy(
                                                        id = ssidId,
                                                        ssid = ssid,
                                                        isChecked = isChecked
                                                    )
                                            )
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

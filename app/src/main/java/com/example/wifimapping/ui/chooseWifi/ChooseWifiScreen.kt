package com.example.wifimapping.ui.chooseWifi

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.wifimapping.ui.viewmodel.RoomParamsViewModel
import com.example.wifimapping.util.CountDownTimer
import com.example.wifimapping.util.getCountDown
import com.example.wifimapping.util.scanWifi
import kotlinx.coroutines.*

object ChooseWifiDestination : NavigationDestination {
    override val route = "choose_wifi"
    override val titleRes = R.string.choose_wifi_title
    const val SSIDARG = "ssid"
}

const val PERMISSIONS_REQUEST_CODE = 1
@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseWifiScreen(
    canNavigateBack: Boolean = false,
    navigateToLocateRouter: (Int) -> Unit,
    viewModel: WifiViewModel = viewModel(factory = AppViewModelProvider.Factory),
    previewGridviewModel: RoomParamsViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val wifiUiStateList by viewModel.allWifiUiStateList.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var wifiList by remember { mutableStateOf(scanWifi(context)) }
    val buttonCounter = remember {
        CountDownTimer(
            initialTime = 27,
            minTime = 1,
        )
    }
    var isWifiRefreshButtonDisabled by remember { mutableStateOf(false) }
    var isNextButtonDisabled by remember { mutableStateOf(true) }

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
                    WifiList(
                        wifiList = wifiList,
                        wifiListDb = wifiUiStateList.wifiList,
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
                        },
                        isNextButtonDisabled = {
                            isNextButtonDisabled = it
                        },
                        resetWifiChecked = {
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.resetCheckedWifi()
                            }
                        }
                    )
                }
                Row {
                    Button(
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier
                            .padding(1.dp)
                            .padding(5.dp),
                        enabled = !isWifiRefreshButtonDisabled,
                        onClick = {
                            isWifiRefreshButtonDisabled = true
                            var scanWifiResult = scanWifi(context)
                            if (scanWifiResult.isNotEmpty()) {
                                wifiList = scanWifiResult
                            }else{
                                Toast.makeText(context,
                                    "Too fast clicking!",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        if (!isWifiRefreshButtonDisabled) {
                            Text("Refresh Wifi")
                        }else{
                            Text("Tunggu ${buttonCounter.getCountDown} s")
                        }
                    }
                    Button(
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            var data = previewGridviewModel.roomParamsUiState.roomParamsDetails
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
                if (isWifiRefreshButtonDisabled) {
                    LaunchedEffect(buttonCounter) {
                        coroutineScope {
                            launch {
                                buttonCounter.run()
                            }
                        }
                    }
                }
                if (buttonCounter.getCountDown == 0){
                    isWifiRefreshButtonDisabled = false
                    buttonCounter.reset()
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun WifiList(
    wifiList: MutableList<Wifi>,
    wifiListDb: List<Wifi>,
    wifiUiState: WifiUiState,
    addUpdateWifi: (WifiDetails) -> Unit,
    insertWifi: () -> Unit,
    updateWifi: () -> Unit,
    isNextButtonDisabled: (Boolean) -> Unit,
    resetWifiChecked: () -> Unit,
){
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
    if (wifiList.isNotEmpty()) {
        var wifiListDisplay: MutableList<Wifi> = ArrayList()
        if (ssidListDb.isNotEmpty()) {
            for (wifi in wifiList) {
                val wifiSsid = wifi.ssid
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
                                        dbm = wifi.dbm
                                    )
                                )
                            }
                        } else {
                            wifiListDisplay.add(
                                Wifi(
                                    id = idListDb[ssidListDb.indexOf(wifiSsid)],
                                    ssid = wifiSsid,
                                    isChecked = false,
                                    dbm = wifi.dbm
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
                    var ssidId by remember { mutableIntStateOf(it.id) }
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clickable {
                                isChecked = !isChecked
                                if (isChecked) {
                                    checkedCount = checkedCount + 1
                                }else{
                                    if (checkedCount > 0) {
                                        checkedCount = checkedCount - 1
                                    }
                                }
                                if (checkedCount > 0){
                                    isNextButtonDisabled(false)
                                }else{
                                    isNextButtonDisabled(true)
                                }

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
                                            if (isChecked) {
                                                checkedCount = checkedCount + 1
                                            }else{
                                                if (checkedCount > 0) {
                                                    checkedCount = checkedCount - 1
                                                }
                                            }
                                            if (checkedCount > 0){
                                                isNextButtonDisabled(false)
                                            }else{
                                                isNextButtonDisabled(true)
                                            }
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

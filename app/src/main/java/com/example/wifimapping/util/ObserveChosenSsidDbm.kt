package com.example.wifimapping.util

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.example.wifimapping.data.Wifi
import com.example.wifimapping.ui.viewmodel.GridUiStateList
import com.example.wifimapping.ui.viewmodel.WifiViewModel
import kotlinx.coroutines.delay

class ObserveChosenSsidDbm(
    private val context: Context,
    private var chosenSsid: Wifi,
    private val gridListDb: GridUiStateList,
    private val wifiViewModel: WifiViewModel
) {
    var dbm by mutableIntStateOf(0)
        private set

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun run() {
        while (true) {
            delay(500)
            var scanWifiResult = scanWifi(context)
            if (chosenSsid.id == 0){
                for (i in gridListDb.gridList) {
                    if (i.idWifi != 0){
                        chosenSsid = wifiViewModel.selectWifiById(i.idWifi)
                    }
                }
            }
            for (wifi in scanWifiResult) {
                if (wifi.ssid == chosenSsid.ssid) {
                    dbm = wifi.dbm
                }
            }
        }
    }
}

val ObserveChosenSsidDbm.getDbm: Int
    get() = dbm
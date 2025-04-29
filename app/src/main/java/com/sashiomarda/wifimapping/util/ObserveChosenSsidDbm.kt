package com.sashiomarda.wifimapping.util

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

class ObserveChosenSsidDbm(
    private val context: Context,
    private var chosenSsidList: MutableList<String>,
) {
    var dbmList by mutableStateOf(listOf<Int>())
        private set

    var ssidList by mutableStateOf(listOf<String>())
        private set

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun run() {
        while (true) {
            var dbms : MutableList<Int> = ArrayList()
            var ssids : MutableList<String> = ArrayList()
            delay(500)
            var scanWifiResult = scanWifi(context)
            for (ssid in chosenSsidList) {
                for (wifi in scanWifiResult) {
                    if (ssid == wifi.ssid) {
                        ssids.add(wifi.ssid)
                        dbms.add(wifi.dbm)
                    }
                }
                if (ssid !in ssids){
                    ssids.add(ssid)
                    dbms.add(-100)
                }
            }
            ssidList = ssids
            dbmList = dbms
        }
    }
}

val ObserveChosenSsidDbm.getDbmList: List<Int>
    get() = dbmList

val ObserveChosenSsidDbm.getSsidList: List<String>
    get() = ssidList
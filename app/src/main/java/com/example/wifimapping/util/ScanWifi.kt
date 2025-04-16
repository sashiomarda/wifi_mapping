package com.example.wifimapping.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.wifimapping.data.Wifi

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun scanWifi(context : Context) : MutableList<Wifi>{
    val wifiManager: WifiManager  = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val activity = context as Activity

    val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            context.unregisterReceiver(this)
        }
    }

    val intentFilter = IntentFilter()
    intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
    context.applicationContext.registerReceiver(wifiScanReceiver, intentFilter)
    val success = wifiManager.startScan()
    var wifiList : MutableList<Wifi> = ArrayList()
    if (wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLED) {
        if (success){
            var results = wifiManager.scanResults
            for (wifi in results){
                if (wifi.SSID.toString() != "") {
                    var ssidText = if (wifi.frequency > 3000) {
                        "${wifi.SSID.toString().removeSurrounding("\"")}_5GHz"
                    } else {
                        wifi.SSID.toString().removeSurrounding("\"")
                    }
                    wifiList.add(
                        Wifi(
                            ssid = ssidText,
                            dbm = wifi.level
                        )
                    )
                }
            }
        }else{
            context.unregisterReceiver(wifiScanReceiver)
        }
    }
    return wifiList
}
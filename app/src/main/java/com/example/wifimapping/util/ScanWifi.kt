package com.example.wifimapping.util

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
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.wifimapping.data.Wifi
import com.example.wifimapping.ui.chooseWifi.PERMISSIONS_REQUEST_CODE

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun scanWifi(context : Context) : MutableList<Wifi>{
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
    val success = wifiManager.startScan()
    var wifiList : MutableList<Wifi> = ArrayList()
    if (wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLED) {
        if (success){
            var results = wifiManager.scanResults
            for (wifi in results){
                wifiList.add(Wifi(
                    ssid = wifi.wifiSsid.toString().removeSurrounding("\""),
                    dbm = wifi.level
                ))
            }

        }
    }
    return wifiList
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
            PERMISSIONS_REQUEST_CODE)
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
            PERMISSIONS_REQUEST_CODE)
        return
    } else {
        wifiManager.scanResults
    }
}

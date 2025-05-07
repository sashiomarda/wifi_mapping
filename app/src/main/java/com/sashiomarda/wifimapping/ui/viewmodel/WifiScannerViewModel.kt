package com.sashiomarda.wifimapping.ui.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class WifiScannerViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext
    private val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val _ssidDbms = MutableStateFlow<List<WifiScan>>(emptyList())
    val ssidDbms: StateFlow<List<WifiScan>> = _ssidDbms

    private var _isRefreshWifi = MutableStateFlow<Boolean>(true)
    val isRefreshWifi: StateFlow<Boolean> = _isRefreshWifi

    private var _screen = MutableStateFlow<String>("")
    val screen: StateFlow<String> = _screen

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        override fun onReceive(context: Context?, intent: Intent?) {
            val results = wifiManager.scanResults
            if (_isRefreshWifi.value) {
                _ssidDbms.value = results.mapNotNull { WifiScan(it.SSID, it.level) }.distinct()
            }
            if (_isRefreshWifi.value && (_screen.value == "choose_wifi")) {
                _isRefreshWifi.value = false
            }
        }
    }

    private var scanningJob: Job? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startScanning() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(receiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION), Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(receiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        }

        scanningJob = viewModelScope.launch {
            while (isActive) {
                wifiManager.startScan()
                delay(500)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        context.unregisterReceiver(receiver)
        scanningJob?.cancel()
    }

    fun updateIsRefreshWifiScan(isRefreshWifi: Boolean){
        _isRefreshWifi.value = isRefreshWifi
    }

    fun updateScreen(screen: String){
        _screen.value = screen
    }
}

data class WifiScan(
    val ssid: String = "",
    val dbm: Int = 0
)
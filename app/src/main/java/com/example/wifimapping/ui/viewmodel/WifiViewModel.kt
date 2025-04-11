/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.wifimapping.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wifimapping.data.Wifi
import com.example.wifimapping.data.WifiRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to validate and insert wifi in the Room database.
 */
class WifiViewModel(
//    savedStateHandle: SavedStateHandle,
    private val wifiRepository: WifiRepository
) : ViewModel() {

    var wifiUiState by mutableStateOf(WifiUiState())
        private set


    var wifiScanList by mutableStateOf(WifiUiStateList())
        private set

    suspend fun saveWifi() {
        wifiRepository.insertWifi(wifiUiState.wifiDetails.toWifi())
    }

    suspend fun deleteWifi() {
        wifiRepository.deleteWifi(wifiUiState.wifiDetails.toWifi())
    }

    suspend fun updateWifi() {
        wifiRepository.updateWifi(wifiUiState.wifiDetails.toWifi())
    }

    val allWifiUiStateList: StateFlow<WifiUiStateList> =
        wifiRepository.getAllWifiStream().map { WifiUiStateList(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = WifiUiStateList()
            )

    val wifiCheckedUiStateList: StateFlow<WifiUiStateList> =
        wifiRepository.getWifiCheckedStream().map { WifiUiStateList(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = WifiUiStateList()
            )

    suspend fun resetCheckedWifi() {
        wifiRepository.resetCheckedWifi()
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateUiState(wifiDetails: WifiDetails) {
        wifiUiState =
            WifiUiState(wifiDetails = wifiDetails)
    }

    suspend fun selectWifiById(wifiId: Int): Wifi {
        var wifiById = wifiRepository.getWifiById(wifiId)
        return  wifiById
    }

    fun updateWifiScanList(wifiList : List<Wifi>) {
        wifiScanList = WifiUiStateList(wifiList)
    }
}
data class WifiUiState(
    val wifiDetails: WifiDetails = WifiDetails()
)

data class WifiDetails(
    val id: Int = 0,
    val ssid: String = "",
    val isChecked: Boolean = false,
    val dbm: Int = 0
)

/**
 * Extension function to convert [WifiDetails] to [Wifi]. If the value of [WifiDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [WifiDetails.quantity] is not a valid [Int], then the quantity will be set to 0
 */
fun WifiDetails.toWifi(): Wifi = Wifi(
    id = id,
    ssid = ssid,
    isChecked = isChecked,
    dbm = dbm
)

/**
 * Extension function to convert [Wifi] to [WifiUiState]
 */
fun Wifi.toWifiUiState(isEntryValid: Boolean = false): WifiUiState = WifiUiState(
    wifiDetails = this.toWifiDetails()
)

/**
 * Extension function to convert [Wifi] to [WifiDetails]
 */
fun Wifi.toWifiDetails(): WifiDetails = WifiDetails(
    id = id,
    ssid = ssid,
)

data class WifiUiStateList(val wifiList: List<Wifi> = listOf())
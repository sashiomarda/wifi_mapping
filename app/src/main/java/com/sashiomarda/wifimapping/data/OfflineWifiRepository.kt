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

package com.sashiomarda.wifimapping.data

import kotlinx.coroutines.flow.Flow

class OfflineWifiRepository(private val wifiDao: WifiDao) : WifiRepository {
    override fun getAllWifiStream(): Flow<List<Wifi>> = wifiDao.getAllWifi()

    override fun getWifiCheckedStream(): Flow<List<Wifi>> = wifiDao.getWifiChecked()

    override fun getWifiStream(ssid: String): Flow<Wifi?> = wifiDao.getWifi(ssid)

    override suspend fun getWifiById(id: Int): Wifi = wifiDao.getWifiById(id)

    override suspend fun insertWifi(wifi: Wifi) = wifiDao.insert(wifi)

    override suspend fun deleteWifi(wifi: Wifi) = wifiDao.delete(wifi)

    override suspend fun updateWifi(wifi: Wifi) = wifiDao.update(wifi)

    override suspend fun resetCheckedWifi() : Int = wifiDao.resetCheckedWifi()
}

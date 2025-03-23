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

package com.example.wifimapping.data

import kotlinx.coroutines.flow.Flow

class OfflineWifiRepository(private val wifiDao: WifiDao) : WifiRepository {
    override fun getAllWifiStream(): Flow<List<Wifi>> = wifiDao.getAllWifi()

    override fun getWifiStream(ssid: String): Flow<Wifi?> = wifiDao.getWifi(ssid)

    override suspend fun insertWifi(item: Wifi) = wifiDao.insert(item)

    override suspend fun deleteWifi(item: Wifi) = wifiDao.delete(item)

    override suspend fun updateWifi(item: Wifi) = wifiDao.update(item)
}

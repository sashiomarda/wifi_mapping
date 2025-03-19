package com.example.wifimapping.data

import kotlinx.coroutines.flow.Flow

class OfflineWifiRepository (private val wifiDao: WifiDao) : WifiRepository {
    override fun getAllWifiStream(): Flow<List<Wifi>> = wifiDao.getAllWifi()

    override fun getWifiByLayerStream(idLayer: Int): Flow<List<Wifi>> = wifiDao.getAllWifiByLayer(idLayer)

    override fun getWifiStream(id: Int): Flow<Wifi?> = wifiDao.getWifi(id)

    override suspend fun insertWifi(wifi: Wifi) = wifiDao.insert(wifi)

    override suspend fun deleteWifi(wifi: Wifi) = wifiDao.delete(wifi)

    override suspend fun updateWifi(wifi: Wifi) = wifiDao.update(wifi)
}

package com.example.wifimapping.data

import kotlinx.coroutines.flow.Flow

interface WifiRepository {
    /**
     * Retrieve all the grids from the the given data source.
     */
    fun getAllWifiStream(): Flow<List<Wifi>>

    fun getWifiByLayerStream(idLayer: Int): Flow<List<Wifi>>

    /**
     * Retrieve an grid from the given data source that matches with the [id].
     */
    fun getWifiStream(id: Int): Flow<Wifi?>

    /**
     * Insert grid in the data source
     */
    suspend fun insertWifi(grid: Wifi)

    /**
     * Delete grid from the data source
     */
    suspend fun deleteWifi(grid: Wifi)

    /**
     * Update grid in the data source
     */
    suspend fun updateWifi(grid: Wifi)
}
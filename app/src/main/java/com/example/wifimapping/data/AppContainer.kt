package com.example.wifimapping.data

import android.content.Context

interface AppContainer {
    val gridssRepository: GridsRepository
    val wifiRepository: WifiRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineGridsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer{
    override val gridssRepository: GridsRepository by lazy {
        OfflineGridsRepository(WifiMappingDatabase.getDatabase(context).gridDao())
    }

    override val wifiRepository: WifiRepository by lazy {
        OfflineWifiRepository(WifiMappingDatabase.getDatabase(context).wifiDao())
    }
}
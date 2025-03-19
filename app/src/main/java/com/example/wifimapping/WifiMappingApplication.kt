package com.example.wifimapping

import android.app.Application
import com.example.wifimapping.data.AppContainer
import com.example.wifimapping.data.AppDataContainer

class WifiMappingApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
package com.example.wifimapping.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "wifi")
data class Wifi(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idLayer: Int,
    val SSID: String
)

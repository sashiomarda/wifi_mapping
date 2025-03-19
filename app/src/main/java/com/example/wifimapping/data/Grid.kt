package com.example.wifimapping.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grids")
data class Grid(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idLayer: Int,
    val dbm: Float,
    val gridX: Float,
    val gridY: Float,
    val isRouterPosition : Boolean
)

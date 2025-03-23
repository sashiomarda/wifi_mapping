package com.example.wifimapping.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WifiDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(wifi: Wifi)

    @Update
    suspend fun update(wifi: Wifi)

    @Delete
    suspend fun delete(wifi: Wifi)

    @Query("SELECT * from wifi WHERE ssid = :ssid")
    fun getWifi(ssid: String): Flow<Wifi>

    @Query("SELECT * from wifi ORDER BY ssid ASC")
    fun getAllWifi(): Flow<List<Wifi>>
}
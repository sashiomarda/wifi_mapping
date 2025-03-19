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

    @Query("SELECT * from wifi ORDER BY id ASC")
    fun getAllWifi(): Flow<List<Wifi>>

    @Query("SELECT * from wifi WHERE idLayer = :idLayer  ORDER BY id ASC")
    fun getAllWifiByLayer(idLayer : Int): Flow<List<Wifi>>

    @Query("SELECT * from wifi WHERE id = :id")
    fun getWifi(id: Int): Flow<Wifi>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(wifi: Wifi)

    @Update
    suspend fun update(wifi: Wifi)

    @Delete
    suspend fun delete(wifi: Wifi)
}
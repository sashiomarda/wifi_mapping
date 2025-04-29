package com.example.wifimapping.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GridDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(grid: Grid)

    @Update
    suspend fun update(grid: Grid)

    @Delete
    suspend fun delete(grid: Grid)

    @Query("SELECT * from grid WHERE idRoom = :idRoom")
    fun getGridByIdRoom(idRoom: Int): Flow<List<Grid>>

    @Query("SELECT * from grid WHERE idHistory = :idHistory")
    fun getGridByIdHistory(idHistory: Int): Flow<List<Grid>>

    @Query("UPDATE grid SET idWifi = 0")
    fun resetInputGrid() : Int

    @Query("SELECT * from grid ORDER BY id DESC LIMIT 1")
    suspend fun getLastGridInputId(): Grid
}
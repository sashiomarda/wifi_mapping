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

    @Query("SELECT * from grids ORDER BY id ASC")
    fun getAllGrids(): Flow<List<Grid>>

    @Query("SELECT * from grids WHERE idLayer = :idLayer  ORDER BY id ASC")
    fun getAllGridsByLayer(idLayer : Int): Flow<List<Grid>>

    @Query("SELECT * from grids WHERE id = :id")
    fun getGrid(id: Int): Flow<Grid>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(grid: Grid)

    @Update
    suspend fun update(grid: Grid)

    @Delete
    suspend fun delete(grid: Grid)
}
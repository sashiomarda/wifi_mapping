package com.sashiomarda.wifimapping.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DbmDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dbm: Dbm)

    @Update
    suspend fun update(dbm: Dbm)

    @Delete
    suspend fun delete(dbm: Dbm)

    @Query("SELECT * from dbm WHERE idHistory = :idHistory ORDER BY idGrid ASC")
    fun getDbmByIdHistory(idHistory: Int): Flow<List<Dbm>>

    @Query("SELECT * from dbm WHERE idHistory = :idHistory " +
            "AND layerNo = :layerNo ORDER BY idGrid ASC")
    suspend fun getDbmByIdHistoryLayerNo(idHistory: Int,layerNo: Int): List<Dbm>

    @Query("SELECT * from dbm WHERE idHistory = :idHistory AND layerNo = :layerNo")
    fun getDbmByIdHistoryAndLayerNo(idHistory: Int,layerNo: Int): Flow<List<Dbm>>

    @Query("SELECT * from dbm ORDER BY id DESC LIMIT 1")
    suspend fun getLastDbm(): Dbm
}
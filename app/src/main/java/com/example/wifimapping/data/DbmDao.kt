package com.example.wifimapping.data

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

    @Query("SELECT * from dbm WHERE idCollectData = :idCollectData ORDER BY idGrid ASC")
    fun getDbmByIdCollectData(idCollectData: Int): Flow<List<Dbm>>

    @Query("SELECT * from dbm WHERE idCollectData = :idCollectData AND layerNo = :layerNo")
    fun getDbmByIdCollectDataAndLayerNo(idCollectData: Int,layerNo: Int): Flow<List<Dbm>>

    @Query("SELECT * from dbm ORDER BY id DESC LIMIT 1")
    suspend fun getLastDbm(): Dbm
}
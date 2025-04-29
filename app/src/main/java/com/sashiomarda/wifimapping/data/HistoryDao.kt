package com.sashiomarda.wifimapping.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(history: History)

    @Update
    suspend fun update(history: History)

    @Delete
    suspend fun delete(history: History)

    @Query("SELECT * from history WHERE idRoom = :idRoom")
    fun getHistoryByIdRoom(idRoom: Int): Flow<List<History>>

    @Query("SELECT h.id, h.timestamp, h.idRoom, r.roomName " +
            "FROM history h JOIN room_params r on h.idRoom = r.id " +
            "WHERE idRoom = :idRoom " +
            "ORDER BY h.timestamp DESC")
    fun getHistoryRoomByIdRoom(idRoom: Int): Flow<List<HistoryRoom>>

    @Query("SELECT * from history WHERE id = :id")
    fun getHistoryById(id: Int): Flow<History>

    @Query("SELECT h.id, h.timestamp, h.idRoom, r.roomName " +
            "FROM history h JOIN room_params r on h.idRoom = r.id " +
            "ORDER BY h.timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryRoom>>

    @Query("SELECT * from history ORDER BY id DESC LIMIT 1")
    suspend fun getLastHistoryId(): History
}
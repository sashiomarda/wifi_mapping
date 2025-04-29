package com.example.wifimapping.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomParamsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(roomParams: RoomParams)

    @Update
    suspend fun update(roomParams: RoomParams)

    @Delete
    suspend fun delete(roomParams: RoomParams)

    @Query("SELECT * from room_params WHERE id = :id")
    fun getRoomParams(id: Int): Flow<RoomParams>

    @Query("SELECT * from room_params ORDER BY roomName ASC")
    fun getAllRoomParams(): Flow<List<RoomParams>>

    @Query("SELECT * from room_params ORDER BY id DESC LIMIT 1")
    fun getLastRoomParamsId(): Flow<RoomParams>
}
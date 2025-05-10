package com.sashiomarda.wifimapping.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ImageFileDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(imageFile: ImageFile)

    @Update
    suspend fun update(imageFile: ImageFile)

    @Delete
    suspend fun delete(imageFile: ImageFile)

    @Query("SELECT * from image_file WHERE idHistory = :idHistory ORDER BY layerNo ASC")
    suspend fun getImageFileByIdHistory(idHistory: Int): List<ImageFile>
}
package com.example.wifimapping.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlin.also
import kotlin.jvm.java

@Database(entities = [RoomParams::class], version = 3, exportSchema = false)
abstract class WifiMappingDatabase : RoomDatabase() {
    abstract fun roomParamsDao(): RoomParamsDao

    companion object {
        @Volatile
        private var Instance: WifiMappingDatabase? = null
        fun getDatabase(context: Context): WifiMappingDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, WifiMappingDatabase::class.java, "wifimapping_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
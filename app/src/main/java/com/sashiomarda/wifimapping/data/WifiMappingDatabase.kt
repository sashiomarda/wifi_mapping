package com.sashiomarda.wifimapping.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlin.also
import kotlin.jvm.java

@Database(entities = [
    RoomParams::class,
    Wifi::class,
    Grid::class,
    Dbm::class,
    History::class,
    ImageFile::class],
    version = 5, exportSchema = false)

abstract class WifiMappingDatabase : RoomDatabase() {
    abstract fun roomParamsDao(): RoomParamsDao
    abstract fun wifiDao(): WifiDao
    abstract fun gridDao(): GridDao
    abstract fun dbmDao() : DbmDao
    abstract fun historyDao() : HistoryDao
    abstract fun imageFileDao() : ImageFileDao

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
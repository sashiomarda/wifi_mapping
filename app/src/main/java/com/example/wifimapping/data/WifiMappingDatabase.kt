package com.example.wifimapping.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Grid::class, Wifi::class], version = 1, exportSchema = false)
abstract class WifiMappingDatabase : RoomDatabase() {

    abstract fun gridDao(): GridDao
    abstract fun wifiDao(): WifiDao
    companion object {
        @Volatile
        private var Instance: WifiMappingDatabase? = null

        fun getDatabase(context: Context): WifiMappingDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, WifiMappingDatabase::class.java, "grid_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
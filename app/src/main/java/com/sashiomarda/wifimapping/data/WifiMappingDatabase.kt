package com.sashiomarda.wifimapping.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
//                    .addMigrations(MIGRATION_4_5) // Tambahkan semua migrasi di sini
                    .build()
                    .also { Instance = it }
            }
        }

    }
}

//val MIGRATION_4_5 = object : Migration(4, 5) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        // Contoh: Menambah kolom baru
//        database.execSQL("ALTER TABLE RoomParams ADD COLUMN new_column TEXT DEFAULT '' NOT NULL")
//    }
//}
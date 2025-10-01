package org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DAO.Journal_DAO
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData

@Database(entities =
[JournalLoacalData::class
], version = 4, exportSchema = false)

abstract class BNA_RoomDatabase : RoomDatabase() {


    abstract fun Journal_DAO():Journal_DAO

    companion object {
        @Volatile
        private var database: BNA_RoomDatabase? = null



        fun getDatabase(context: Context): BNA_RoomDatabase {
            return database ?: synchronized(this) {
                database ?: buildDatabase(context).also { database = it }
            }
        }

        private fun buildDatabase(context: Context): BNA_RoomDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                BNA_RoomDatabase::class.java, "BNA_Room_Database"
            ).fallbackToDestructiveMigration()
                .build()
        }





    }

    // Add a suspend function to perform database operations on a background thread
    suspend fun performDatabaseOperation(operation: suspend () -> Unit) {
        withContext(Dispatchers.IO) {
            operation()
        }
    }



}


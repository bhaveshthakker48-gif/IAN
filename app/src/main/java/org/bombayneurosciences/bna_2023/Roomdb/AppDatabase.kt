package org.bombayneurosciences.bna_2023.Roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NotificationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
abstract fun notificationDao() : NotificationDao
    companion object {
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (Instance == null) {
                Instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "appdatabaseDB"
                ).build()
            }
            return Instance!!
        }
    }
}

package org.bombayneurosciences.bna_2023.Roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFile(downloadedFile: NotificationEntity)

    @Query("SELECT * FROM notifications")
    fun getAllDownloadedFiles(): List<NotificationEntity>
    @Query("SELECT * FROM notifications ORDER BY id DESC LIMIT 1")
    fun getLatestNotification(): NotificationEntity?

}


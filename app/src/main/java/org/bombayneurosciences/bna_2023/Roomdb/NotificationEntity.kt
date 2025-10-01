package org.bombayneurosciences.bna_2023.Roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val attachment: String,
    val fileName: String,

    )

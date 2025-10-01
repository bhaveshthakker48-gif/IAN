package org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Entity(tableName = "JournalLoacalData")
data class JournalLoacalData(
    @PrimaryKey(autoGenerate = true) val index: Int,
    val id: Int,
    val issueId: String,
    val month: String,
    val year: String,
    val articleType: String,
    val title: String,
    val author: String,
    val reference: String,
    val indexPage: Int,
    val noOfPage: Int,
    val articleFile: String,
    val isArchive: Int,
    val isActive: Int,
    val isDeleted: Int,
    val createdAt: String,
    val updatedAt: String,
    val issueFile: String,
    val volume:Int,
    val issue_no:Int,
    ):Serializable

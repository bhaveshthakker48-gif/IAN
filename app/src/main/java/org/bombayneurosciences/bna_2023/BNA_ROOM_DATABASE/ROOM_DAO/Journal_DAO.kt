package org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData

@Dao
interface Journal_DAO {


    @Query("SELECT * FROM JournalLoacalData")
    fun getAllJournalLoacalData(): LiveData<List<JournalLoacalData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertJournalLoacalData(JournalLoacalData: JournalLoacalData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertJournal(journalLoacalDataList: MutableList<JournalLoacalData>)

    @Query("DELETE FROM JournalLoacalData")
    suspend fun delete_All_Journal()
}
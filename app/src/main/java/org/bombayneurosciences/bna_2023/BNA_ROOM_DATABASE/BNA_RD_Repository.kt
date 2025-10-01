package org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE

import androidx.lifecycle.LiveData
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DAO.Journal_DAO
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData


class BNA_RD_Repository(
                        private val Journal_DAO: Journal_DAO,

                        private val database: BNA_RoomDatabase) {


    val getAll_Journal: LiveData<List<JournalLoacalData>> = Journal_DAO.getAllJournalLoacalData()


    suspend fun insertJournal(journalLoacalDataList: MutableList<JournalLoacalData>) {

        database.performDatabaseOperation {
            Journal_DAO.insertJournal(journalLoacalDataList)

        }

    }

    suspend fun deleteJournal() {
        Journal_DAO.delete_All_Journal()
    }


}






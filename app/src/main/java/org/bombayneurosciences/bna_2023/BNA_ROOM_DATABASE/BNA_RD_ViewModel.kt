package org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData


class BNA_RD_ViewModel(private val repository: BNA_RD_Repository) : ViewModel() {

    val get_All_journal:LiveData<List<JournalLoacalData>> = repository.getAll_Journal

    private val _journalData = MutableLiveData<List<JournalLoacalData>>()
    val journalData: LiveData<List<JournalLoacalData>> get() = _journalData



    fun insertJournal(journalLoacalDataList: MutableList<JournalLoacalData>) {

        viewModelScope.launch {

            try {
                // progressDialog.show()
                repository.insertJournal(journalLoacalDataList)

              //  _saveSuccess.postValue("Success")

                // progressDialog.dismiss()
            } catch (e: Exception) {
                // progressDialog.dismiss()
              //  _saveSuccess.postValue("Failure")
            }


        }

    }

    fun deleteJournal() {
        viewModelScope.launch {
            repository.deleteJournal()
        }
    }

    // This method sets the original data
    fun setJournalData(entries: List<JournalLoacalData>) {
        _journalData.value = entries
    }

    // This method filters the data for unique month/year combinations
    fun getUniqueJournalEntries(entries: List<JournalLoacalData>): List<JournalLoacalData> {
        val uniqueEntries = LinkedHashSet<String>()
        val uniqueList = mutableListOf<JournalLoacalData>()

        for (entry in entries) {
            val uniqueKey = entry.month + entry.year
            if (!uniqueEntries.contains(uniqueKey)) {
                uniqueEntries.add(uniqueKey)
                uniqueList.add(entry)
            }
        }

        return uniqueList
    }

    // This method can be called to get the filtered list
    fun getFilteredJournalData(): LiveData<List<JournalLoacalData>> {
        val filteredData = _journalData.value?.let { getUniqueJournalEntries(it) }
        return MutableLiveData(filteredData)
    }


}


package org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BNA_RD_ViewModelFactory(val repository: BNA_RD_Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BNA_RD_ViewModel::class.java)) {
            return BNA_RD_ViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

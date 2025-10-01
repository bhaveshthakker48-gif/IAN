package com.org.wfnr_2024.ViewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BNAProviderFactory(val BNARespository: BNARespository, val application: Application):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BNA_ViewModel(BNARespository,application) as T
    }
}
package com.org.wfnr_2024.ViewModel

import org.bombayneurosciences.bna_2023.Data.BNA_RetrofitInstance


class BNARespository() {
    suspend fun getJournalData()= BNA_RetrofitInstance.productionApi.getJournalData()


}
package org.bombayneurosciences.bna_2023.Data

import okhttp3.Response
import org.bombayneurosciences.bna_2023.Model.Journal1.JournalDataResponse
import org.bombayneurosciences.bna_2023.Model.Journal1.JournalResponseItem

import retrofit2.http.GET


interface BNA_API {


    @GET("articles")
    //suspend fun getJournalData():Response<JournalDataResponse>
    suspend fun getJournalData(): List<JournalDataResponse.JournalDataResponseItem>




}
package org.bombayneurosciences.bna_2023.service

import org.bombayneurosciences.bna.Model.Journal.JournalResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiServiceJournal {
    // Define endpoints for latest and archive journal entries
    @GET("journals")
    fun getJournalEntries(): Call<JournalResponse>


    @GET("journals")
    fun getArchiveJournalEntries(): Call<JournalResponse>


}

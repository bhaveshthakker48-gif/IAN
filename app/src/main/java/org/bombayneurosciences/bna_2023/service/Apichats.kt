package org.bombayneurosciences.bna_2023.service

import com.google.gson.GsonBuilder
import org.bombayneurosciences.bna_2023.Model.chats.ChatsResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface Apichats {
    @GET("getcomments")
    fun getComments(@Query("case_id") caseId: Int): Call<List<ChatsResponse>>


}
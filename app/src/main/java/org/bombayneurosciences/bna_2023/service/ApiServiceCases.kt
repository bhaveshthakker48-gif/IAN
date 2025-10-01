package org.bombayneurosciences.bna_2023.service

// ApiServiceCases.kt

import org.bombayneurosciences.bna_2023.JournalNewFolder.Welcome
import retrofit2.Call
import retrofit2.http.GET
import org.bombayneurosciences.bna_2023.Model.CaseofMonth.CaseOfMonth
import org.bombayneurosciences.bna_2023.Model.chats.receivecomments.receivechats
import retrofit2.http.Query

interface ApiServiceCases {

    @GET("getcases")
    fun getCases(): Call<CaseOfMonth>
    @GET("BNA/public/getrecomments")
    fun getComments(@Query("case_id") caseId: Int): Call<receivechats>

    @GET("subsectionArtice")
    fun getCases1(): Call<Welcome>
}

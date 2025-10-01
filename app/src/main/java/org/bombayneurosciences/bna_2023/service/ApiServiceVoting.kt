package org.bombayneurosciences.bna_2023.service

import org.bombayneurosciences.bna_2023.Model.Voting.voting
import org.bombayneurosciences.bna_2023.Model.sesssions.Data
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServiceVoting {
    @GET("public/getquestions")
    fun getQuestions(@Query("tid") topicId: Int): Call<List<Data>>
}

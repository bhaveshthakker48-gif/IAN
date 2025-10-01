package org.bombayneurosciences.bna_2023.service

import org.bombayneurosciences.bna_2023.Model.Topics.Data
import org.bombayneurosciences.bna_2023.Model.Topics.EventTopic
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceTopics {
    @GET("gettopics?")
    fun getTopics(@Query("eid") eventId: Int): Call<List<EventTopic>>

}

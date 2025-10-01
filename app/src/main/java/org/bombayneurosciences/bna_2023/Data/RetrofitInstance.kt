package org.bombayneurosciences.bna_2023.Data

import ApiServiceEvents
import okhttp3.OkHttpClient
import org.bombayneurosciences.bna_2023.service.ApiSection
import org.bombayneurosciences.bna_2023.service.ApiServiceCases
import org.bombayneurosciences.bna_2023.service.ApiServiceJournal
import org.bombayneurosciences.bna_2023.service.ApiServiceLogin
import org.bombayneurosciences.bna_2023.service.ApiServiceNotification
import org.bombayneurosciences.bna_2023.service.ApiServiceSumitRequest
import org.bombayneurosciences.bna_2023.service.ApiServiceTopics
import org.bombayneurosciences.bna_2023.service.ApiToken
import org.bombayneurosciences.bna_2023.service.Apichats
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitInstance {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://www.telemedocket.com/BNA/public/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().build())
        .build()

    val apiServiceNotification = retrofit.create(ApiServiceNotification::class.java)
    val apiServiceJournal = retrofit.create(ApiServiceJournal::class.java)
    val apiServiceEvents = retrofit.create(ApiServiceEvents::class.java)
    val apiServiceTopics = retrofit.create(ApiServiceTopics::class.java)
    val apiToken = retrofit.create(ApiToken::class.java)
    val apiSection = retrofit.create(ApiSection::class.java)

    val apiServiceLogin = retrofit.create(ApiServiceLogin::class.java)
    val apiServiceSumitRequest = retrofit.create(ApiServiceSumitRequest::class.java)
    val apiServiceCases = retrofit.create(ApiServiceCases::class.java)
    val chatservice = retrofit.create(Apichats::class.java)



}

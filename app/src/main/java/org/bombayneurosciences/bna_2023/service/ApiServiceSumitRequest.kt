package org.bombayneurosciences.bna_2023.service

import org.bombayneurosciences.bna_2023.Model.Voting.SubmitResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServiceSumitRequest {

 /*   @GET("submitanswer")
    fun submitAnswer(
       interface ApiServiceSumitRequest {

    @GET("submitanswer")
    suspend fun submitAnswer(
        @Query("event_id") eventId: String,
        @Query("session_id") sessionId: String,
        @Query("topic_id") topicId: String,
        @Query("question_id") questionId: String,
        @Query("question") question: String,
        @Query("user_id") userId: String,
        @Query("cd_start_time") cdStartTime: String,
        @Query("is_correct") isCorrect: String,
        @Query("answer") answer: String
    ): Response<SubmitResponse>
}

    ): Response<SubmitResponse>*/


    @GET("submitanswer?")
    fun submitAnswer(

        @Query("event_id") eventId: String,
        @Query("session_id") sessionId: String,
        @Query("topic_id") topicId: String,
        @Query("question_id") questionId: String,
        @Query("question") question: String,
        @Query("user_id") userId: String,
        @Query("cd_start_time") cdStartTime: String,
        @Query("is_correct") isCorrect: String,
        @Query("answer") answer: String,
        @Query("is_dummy") isDummy: String,
    ): Response<SubmitResponse>
    }



package org.bombayneurosciences.bna_2023.service

import org.bombayneurosciences.bna_2023.Model.ApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServiceLogin {

    @GET("memberlogin")
    fun login(
        @Query("membershipno") membershipNo: String,
        @Query("mobemail") mobEmail: String,
        @Query("phoneno") phoneNo: String?

    ): Call<ApiResponse>



}
package org.bombayneurosciences.bna_2023.service

import org.bombayneurosciences.bna_2023.Model.NotificationRead
import org.bombayneurosciences.bna_2023.Model.Token
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiToken {
    @GET("addDevicetoken")
    fun addDeviceToken(
        @Query("DeviceToken") DeviceToken: String,
        @Query("UDID_no") UDID_no: String,
        @Query("Os_name") Os_name: String,
        @Query("location") location: String,
        @Query("Platform") Platform: String
    ): Call<Token>

    @GET("notification-receipt")
    fun notificationRecieve(
        @Query("device_token") device_token: String,
        @Query("notification_id") notification_id: String,
        @Query("received_at") received_at: String,
        @Query("received_by") received_by: String
    ): Call<NotificationRead>
}
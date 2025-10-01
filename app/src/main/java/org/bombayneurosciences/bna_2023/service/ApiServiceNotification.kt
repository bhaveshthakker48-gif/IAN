package org.bombayneurosciences.bna_2023.service

import org.bombayneurosciences.bna_2023.Model.Notification.NotificationDataClass
import retrofit2.Call
import retrofit2.http.GET

interface ApiServiceNotification {

    @GET("notifications")
    fun getNotifications(): Call<NotificationDataClass>

}
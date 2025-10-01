package org.bombayneurosciences.bna_2023.Model.Events

data class Event(
    val NOofsession: Int,
    val created_at: String,
    val end_date: String,
    val end_time: String,
    val end_to_time: String,
    val etype: String,
    val id: Int,
    val is_active: Int,
    val is_deleted: Int,
    val name: String,
    val organizers: String,
    val password: String,
    val start_date: String,
    val start_time: String,
    val start_to_time: String,
    val updated_at: String,
    val username: String,
    val venue: String,

    val serverDate: String? = null

)

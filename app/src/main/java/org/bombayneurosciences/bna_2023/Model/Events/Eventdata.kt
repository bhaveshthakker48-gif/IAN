package org.bombayneurosciences.bna_2023.Model.Events

data class Eventdata(
    val `data`: List<Data>,

    val NOofsession: String,
    val created_at: String,
    val end_date: String,
    val end_time: String,
    val end_to_time: String,
    val etype: String,
    val id: String,
    val is_active: String,
    val is_deleted: String,
    val name: String,
    val organizers: String,
    val password: String,
    val start_date: String,
    val start_time: String,
    val start_to_time: String,
    val updated_at: String,
    val username: String,
    val venue: String
)


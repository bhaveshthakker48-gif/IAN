package org.bombayneurosciences.bna_2023.Model.chats.receivecomments

data class Data(
    val case_id: Int,
    val commFrom: String,
    val commFromid: Int,
    val commFromname: String,
    val comment: String,
    val created_at: String,
    val id: Int,
    val is_replied: Int,
    val reTocommid: Int,
    val represent: Int
)
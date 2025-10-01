package org.bombayneurosciences.bna_2023.Model.chats

data class Data(
    val case_id: Int,
    val commFrom: String,
    val commFromid: Int,
    val comment: String,
    val commFromname : String,
    val created_at: String,
    val id: Int,
    val is_replied: String,
    val reTocommid: Int,
    val represent: Int,
    var replyCount: Int = 0


)
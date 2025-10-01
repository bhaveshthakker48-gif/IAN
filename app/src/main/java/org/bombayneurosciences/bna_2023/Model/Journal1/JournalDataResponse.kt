package org.bombayneurosciences.bna_2023.Model.Journal1

class JournalDataResponse : ArrayList<JournalDataResponse.JournalDataResponseItem>(){
    data class JournalDataResponseItem(
        val articleFile: String,
        val article_type: String,
        val author: String,
        val created_at: String,
        val id: Int,
        val index_page: Int,
        val is_active: Int,
        val is_archive: Int,
        val is_deleted: Int,
        val issueFile: String,
        val issue_id: String,
        val issue_no: Int,
        val month: String,
        val no_of_page: Int,
        val reference: String,
        val title: String,
        val updated_at: String,
        val volume: Int,
        val year: String
    )
}
package org.bombayneurosciences.bna_2023.Model.Journal1

import com.google.gson.annotations.SerializedName



data class JournalResponseItem(
    @SerializedName("id") val id: Int,
    @SerializedName("issue_id") val issueId: String,
    @SerializedName("month") val month: String,
    @SerializedName("year") val year: String,
    @SerializedName("article_type") val articleType: String?,
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("reference") val reference: String,
    @SerializedName("index_page") val indexPage: Int,
    @SerializedName("no_of_page") val noOfPage: Int,
    @SerializedName("articleFile") val articleFile: String,
    @SerializedName("is_archive") val isArchive: Int,
    @SerializedName("is_active") val isActive: Int,
    @SerializedName("is_deleted") val isDeleted: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("issueFile") val issueFile: String
)



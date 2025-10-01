package org.bombayneurosciences.bna_2023.Model

data class PdfFileInfo(
    val month: String,
    val year: String,
    val indexPage: Int,
    val issue_fileName: String,
    val article_fileName: String
)


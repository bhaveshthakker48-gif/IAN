package org.bombayneurosciences.bna.Model.Journal

data class JournalData(
    val month: String,
    val year: String,
    val data: List<JournalEntry>
)

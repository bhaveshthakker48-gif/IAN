package org.bombayneurosciences.bna_2023.Model.CaseofMonth

data class MediaItem(val type: MediaType, val path: String)

enum class MediaType {
    R_IMAGE,
    P_IMAGE,
    VIDEO
}



package org.bombayneurosciences.bna_2023.Model

import org.bombayneurosciences.bna_2023.Model.Login.Data

data class ApiResponse(
    val `data`: List<Data>,
    val success: Int
)

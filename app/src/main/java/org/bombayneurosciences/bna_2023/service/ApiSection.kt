package org.bombayneurosciences.bna_2023.service

import org.bombayneurosciences.bna_2023.Model.CaseofMonth.Section
import retrofit2.http.GET

interface ApiSection {
    @GET("getcases")
     fun getSections(): List<Section>
}

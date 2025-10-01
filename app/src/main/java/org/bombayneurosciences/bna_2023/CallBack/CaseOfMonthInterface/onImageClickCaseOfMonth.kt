package org.bombayneurosciences.bna_2023.CallBack.CaseOfMonthInterface

import org.bombayneurosciences.bna_2023.Model.CaseofMonth.Data

interface onImageClickCaseOfMonth {

    fun onImageCaseItemClick(position: Int, imagepath:String, mediaType:String)
    fun speak()

}
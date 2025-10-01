package org.bombayneurosciences.bna_2023.CallBack.CaseOfMonthInterface

import org.bombayneurosciences.bna_2023.Model.CaseofMonth.DataX

interface CaseItemClickListenerCaseOfMonth {

    fun onCaseItemClick(position: Int, data: DataX)
}
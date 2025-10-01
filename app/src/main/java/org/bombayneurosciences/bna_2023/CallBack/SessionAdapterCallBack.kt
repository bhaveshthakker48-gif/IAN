package org.bombayneurosciences.bna_2023.CallBack

import org.bombayneurosciences.bna_2023.Model.sesssions.Data

interface SessionAdapterCallBack {
    fun onItemClick(position:Int,data: Data)

}
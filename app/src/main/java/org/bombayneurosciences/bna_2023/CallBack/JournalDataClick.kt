package org.bombayneurosciences.bna_2023.CallBack

import android.graphics.Bitmap
import android.view.View
import org.bombayneurosciences.bna_2023.BNA_ROOM_DATABASE.ROOM_DATABASE_MODEL.JournalLoacalData
import org.bombayneurosciences.bna_2023.JournalNewFolder.WelcomeElement

interface JournalDataClick {

    fun JournalItemClicked(data:JournalLoacalData,position:Int,view: View)
    fun JournalItemClicked1(data:WelcomeElement,position:Int,view: View)
    fun ItemClicked(data:Bitmap,position:Int)
}
// NewInterface.kt
package org.bombayneurosciences.bna_2023.CallBack

import org.bombayneurosciences.bna_2023.Model.Topic_new.Data

interface NewInterface {
    fun onNewItemClick(data: Data, position: Int)
}

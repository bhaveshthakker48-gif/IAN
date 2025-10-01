package org.bombayneurosciences.bna_2023.Model.Events

import android.os.Bundle
import java.io.Serializable

data class EventItem(
    val id :Int,
    val name: String,
    val startDate: String,
    val endDate: String,
    val venue: String,
    val meetingType: String
) : Serializable {
    companion object {
        fun Bundle.putSerializable(key: String, value: Serializable) {
            this.putSerializable(key, value)
        }
    }
}

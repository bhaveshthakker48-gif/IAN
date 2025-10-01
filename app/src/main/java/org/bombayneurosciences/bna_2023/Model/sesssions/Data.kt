package org.bombayneurosciences.bna_2023.Model.sesssions

import android.os.Parcel
import android.os.Parcelable

data class Data(
    val chairpersons: String,
    val created_at: String,
    val end_time: String,
    val event_date: String,
    val event_day: Int,
    val event_id: Int,
    val id: Int,
    val is_active: Int,
    val is_deleted: Int,
    val sno: Int,
    val start_time: String,
    val title: String,
    val type: String,
    val updated_at: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(chairpersons)
        parcel.writeString(created_at)
        parcel.writeString(end_time)
        parcel.writeString(event_date)
        parcel.writeInt(event_day)
        parcel.writeInt(event_id)
        parcel.writeInt(id)
        parcel.writeInt(is_active)
        parcel.writeInt(is_deleted)
        parcel.writeInt(sno)
        parcel.writeString(start_time)
        parcel.writeString(title)
        parcel.writeString(type)
        parcel.writeString(updated_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }
}

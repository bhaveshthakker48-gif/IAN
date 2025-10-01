package org.bombayneurosciences.bna_2023.Model.CaseofMonth

import android.os.Parcel
import android.os.Parcelable

data class Data(
    val description: String,
    val description_plain: String,
    val designation: String,
    val email: String,
    val end_date: String,
    val case_id: Int,
    val mobile: String,
    val name: String,
    val pImages: String,
    val rImages: String,
    val start_date: String,
    val title: String,
    val title_plain: String,
    val videos: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(description)
        parcel.writeString(description_plain)
        parcel.writeString(designation)
        parcel.writeString(email)
        parcel.writeString(end_date)
        parcel.writeInt(case_id)
        parcel.writeString(mobile)
        parcel.writeString(name)
        parcel.writeString(pImages)
        parcel.writeString(rImages)
        parcel.writeString(start_date)
        parcel.writeString(title)
        parcel.writeString(title_plain)
        parcel.writeString(videos)
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

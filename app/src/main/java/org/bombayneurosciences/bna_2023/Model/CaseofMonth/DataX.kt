package org.bombayneurosciences.bna_2023.Model.CaseofMonth

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

data class DataX(
    val case_id: Int,
    val description: String?,
    val description_plain: String?,
    val designation: String?,
    val email: String?,
    val end_date: String?,
    val mobile: String?,
    val name: String?,
    val sections: ArrayList<Section>?,
    val start_date: String?,
    val title: String?,
    val title_plain: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(Section),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(case_id)
        parcel.writeString(description)
        parcel.writeString(description_plain)
        parcel.writeString(designation)
        parcel.writeString(email)
        parcel.writeString(end_date)
        parcel.writeString(mobile)
        parcel.writeString(name)
        parcel.writeTypedList(sections)
        parcel.writeString(start_date)
        parcel.writeString(title)
        parcel.writeString(title_plain)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataX> {
        override fun createFromParcel(parcel: Parcel): DataX {
            return DataX(parcel)
        }

        override fun newArray(size: Int): Array<DataX?> {
            return arrayOfNulls(size)
        }
    }
}
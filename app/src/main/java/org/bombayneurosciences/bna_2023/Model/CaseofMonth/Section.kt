package org.bombayneurosciences.bna_2023.Model.CaseofMonth

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

data class Section(
    val description: String?,
    val description_plain: String?,
    val media: ArrayList<Media>?,  // Assuming Media is Parcelable
    val title: String?,
    val title_plain: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
       parcel.createTypedArrayList(Media),  // Read the ArrayList of Media
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(description)
        parcel.writeString(description_plain)
       parcel.writeTypedList(media)  // Write the ArrayList of Media
        parcel.writeString(title)
        parcel.writeString(title_plain)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Section> {
        override fun createFromParcel(parcel: Parcel): Section {
            return Section(parcel)
        }

        override fun newArray(size: Int): Array<Section?> {
            return arrayOfNulls(size)
        }
    }
}

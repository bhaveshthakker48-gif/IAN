package org.bombayneurosciences.bna_2023.Model.CaseofMonth

import android.os.Parcel
import android.os.Parcelable

data class Media(
    val imagelable: String?,
    val imagename: String?,
    val imageno: Int,
    val imagetype: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imagelable)
        parcel.writeString(imagename)
        parcel.writeInt(imageno)
        parcel.writeString(imagetype)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Media> {
        override fun createFromParcel(parcel: Parcel): Media {
            return Media(parcel)
        }

        override fun newArray(size: Int): Array<Media?> {
            return arrayOfNulls(size)
        }
    }
}
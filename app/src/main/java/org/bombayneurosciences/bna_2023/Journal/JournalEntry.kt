package org.bombayneurosciences.bna.Model.Journal

import android.os.Parcel
import android.os.Parcelable

data class JournalEntry(
    val title: String,
    val author: String,
    val reference: String,
    val articleFile: String,
    val is_archive: Int,
    val year: String,
    val month: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeString(reference)
        parcel.writeString(articleFile)
        parcel.writeInt(is_archive)
        parcel.writeString(year)
        parcel.writeString(month)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun monthToNumber() {

    }

    companion object CREATOR : Parcelable.Creator<JournalEntry> {
        override fun createFromParcel(parcel: Parcel): JournalEntry {
            return JournalEntry(parcel)
        }

        override fun newArray(size: Int): Array<JournalEntry?> {
            return arrayOfNulls(size)
        }
    }
}


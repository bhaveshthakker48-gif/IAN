package org.bombayneurosciences.bna_2023.Model.Notification

import android.os.Parcel
import android.os.Parcelable

data class Data(
    val attachment: String,
    val content: String,
    val created_at: String,
    val id: Int,
    val isactive: Int,
    val link_to: String,
    val modified_at: String,
    val s_date: String,
    val s_time: String,
    val sent_to_android: Int,
    val sent_to_ios: Int,
    val title: String,
    val total: Int,
    var isRead: Boolean = false,  // Add this property
    val timestamp: Long
) : Parcelable {
    var url: String = ""
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readByte() != 0.toByte(), // Read isRead as a boolean
        parcel.readLong() // Read timestamp as Long
    ) {
        // Read the URL from the parcel
        url = parcel.readString() ?: ""
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(attachment)
        dest.writeString(content)
        dest.writeString(created_at)
        dest.writeInt(id)
        dest.writeInt(isactive)
        dest.writeString(link_to)
        dest.writeString(modified_at)
        dest.writeString(s_date)
        dest.writeString(s_time)
        dest.writeInt(sent_to_android)
        dest.writeInt(sent_to_ios)
        dest.writeString(title)
        dest.writeInt(total)
        dest.writeByte(if (isRead) 1 else 0) // Write isRead as a byte
        dest.writeLong(timestamp) // Write timestamp as Long
        dest.writeString(url)

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

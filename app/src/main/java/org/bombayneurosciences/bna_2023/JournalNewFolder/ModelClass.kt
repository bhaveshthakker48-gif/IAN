package org.bombayneurosciences.bna_2023.JournalNewFolder

import android.os.Parcel
import android.os.Parcelable


typealias Welcome = ArrayList<WelcomeElement>

/*data class Welcome ( val data: ArrayList<WelcomeElement>
)*/



data class WelcomeElement (
    val id: Long,
    val title: String,
    val article_type: String,
    val month: String,
    val year: String,
    val author: String? = null,
    val cover: String? = null,
    val reference: String? = null,
    val index_page: Long? = null,
    val no_of_page: Long? = null,
    val volume: Int ,
    val issue_no: Int ,
    val subsections: List<Subsection>,

): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.createTypedArrayList(Subsection.CREATOR) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(article_type)
        parcel.writeString(month)
        parcel.writeString(year)
        parcel.writeString(author)
        parcel.writeString(cover)
        parcel.writeString(reference)
        parcel.writeValue(index_page)
        parcel.writeValue(no_of_page)
        parcel.writeInt(volume)
        parcel.writeInt(issue_no)
        parcel.writeTypedList(subsections)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WelcomeElement> {
        override fun createFromParcel(parcel: Parcel): WelcomeElement {
            return WelcomeElement(parcel)
        }

        override fun newArray(size: Int): Array<WelcomeElement?> {
            return arrayOfNulls(size)
        }
    }
}



data class WelcomeElement1 (
    val cover: String? = null
)

data class Image (
    val img_no: Long,
    val img_label: String,
    val img_url: String,
    val imagetype: String?="Video0"


): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(img_no)
        parcel.writeString(img_label)
        parcel.writeString(img_url)
        parcel.writeString(imagetype)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel): Image {
            return Image(parcel)
        }

        override fun newArray(size: Int): Array<Image?> {
            return arrayOfNulls(size)
        }
    }
}

enum class Month {
    April,
    August,
    December,
    February,
    January,
    July,
    June,
    March,
    May,
    November,
    September
}

data class Subsection (
    val id: Long,
    val subdescription: String,
    val absubdescription: String,
    val images: List<Image>
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createTypedArrayList(Image.CREATOR) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(subdescription)
        parcel.writeString(absubdescription)
        parcel.writeTypedList(images)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Subsection> {
        override fun createFromParcel(parcel: Parcel): Subsection {
            return Subsection(parcel)
        }

        override fun newArray(size: Int): Array<Subsection?> {
            return arrayOfNulls(size)
        }
    }
}

typealias DataFilter = List<MonthArticles1>;
data class MonthArticles1(
    val month: String,
    val cover: String,
    val year: String,
    val issue: String,
    val volume: String,
    val articles: List<WelcomeElement>
)
data class MonthArticles(
    val month: String,

    val articles: List<WelcomeElement>
)

data class YearArticles(
    val year: String,
    val articles: List<WelcomeElement>
)

data class YearArticles1(
    val year: String,
    val months: List<MonthArticles>
)
data class YearArticles2(
    val year: String,
    val months: List<MonthArticles1>
)

/*data class YearArticles(
    val year: String,
    val articles: List<WelcomeElement>
)*/

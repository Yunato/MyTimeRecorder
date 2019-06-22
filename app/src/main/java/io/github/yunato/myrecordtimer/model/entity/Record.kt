package io.github.yunato.myrecordtimer.model.entity

import android.os.Parcel
import android.os.Parcelable

data class Record(val id: String?,
                  val start: Long,
                  val end: Long,
                  val title: String?,
                  val memo: String?,
                  val eval: Int) : Parcelable {

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Record> = object : Parcelable.Creator<Record> {
            override fun createFromParcel(source: Parcel): Record = source.run {
                Record(readString(), readLong(), readLong(), readString(), readString(), readInt())
            }

            override fun newArray(size: Int): Array<Record?> = arrayOfNulls(size)
        }
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.run {
            writeString(id)
            writeLong(start)
            writeLong(end)
            writeString(title)
            writeString(memo)
            writeInt(eval)
        }
    }
}

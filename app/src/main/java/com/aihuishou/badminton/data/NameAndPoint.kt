package com.aihuishou.badminton.data

import android.os.Parcel
import android.os.Parcelable

data class NameAndPoint(
    val id: Int,
    val name: String?,
    val point: Int?,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeValue(point)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NameAndPoint> {
        override fun createFromParcel(parcel: Parcel): NameAndPoint {
            return NameAndPoint(parcel)
        }

        override fun newArray(size: Int): Array<NameAndPoint?> {
            return arrayOfNulls(size)
        }
    }
}
